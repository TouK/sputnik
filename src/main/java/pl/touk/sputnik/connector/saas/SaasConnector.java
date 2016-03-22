package pl.touk.sputnik.connector.saas;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.Connector;
import pl.touk.sputnik.connector.github.GithubPatchset;
import pl.touk.sputnik.connector.http.HttpConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
@Slf4j
public class SaasConnector implements Connector {

    private HttpConnector httpConnector;
    private GithubPatchset githubPatchset;
    private String apiKey;

    public SaasConnector(HttpConnector httpConnector, GithubPatchset githubPatchset) {
        this(httpConnector, githubPatchset, null);
    }

    private static final String API_KEY_PARAM = "key";
    private static final String FILES_URL_FORMAT = "/api/github/%s/pulls/%d/files";
    private static final String VIOLATIONS_URL_FORMAT = "/api/github/%s/pulls/%d/violations";

    @NotNull
    @Override
    public String listFiles() throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(githubPatchset, FILES_URL_FORMAT), apiKeyParam());
        HttpGet request = new HttpGet(uri);
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(request);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    @NotNull
    @Override
    public String sendReview(String violationsAsJson) throws URISyntaxException, IOException {
        log.info("Sending violations: {}", violationsAsJson);
        URI uri = httpConnector.buildUri(createUrl(githubPatchset, VIOLATIONS_URL_FORMAT), apiKeyParam());
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(violationsAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpPost);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    private String createUrl(GithubPatchset patchset, String formatUrl) {
        return String.format(formatUrl, patchset.getProjectPath(), patchset.getPullRequestId());
    }

    @NotNull
    private NameValuePair[] apiKeyParam() {
        if (apiKey != null) {
            return new BasicNameValuePair[]{new BasicNameValuePair(API_KEY_PARAM, apiKey)};
        } else {
            return new BasicNameValuePair[]{};
        }
    }
}
