package pl.touk.sputnik.connector.saas;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.Connector;
import pl.touk.sputnik.connector.Patchset;
import pl.touk.sputnik.connector.http.HttpConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class SaasConnector implements Connector {

    private HttpConnector httpConnector;
    private Patchset patchset;
    private String apiKey;
    private String buildId;

    private static final String API_KEY_PARAM = "key";
    private static final String BUILD_ID_PARAM = "build_id";
    private static final String FILES_URL_FORMAT = "/api/%s/%s/pulls/%d/files";
    private static final String VIOLATIONS_URL_FORMAT = "/api/%s/%s/pulls/%d/violations";

    @NotNull
    @Override
    public String listFiles() throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(patchset, FILES_URL_FORMAT), params());
        HttpGet request = new HttpGet(uri);
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(request);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    @NotNull
    @Override
    public String sendReview(String violationsAsJson) throws URISyntaxException, IOException {
        log.info("Sending violations: {}", violationsAsJson);
        URI uri = httpConnector.buildUri(createUrl(patchset, VIOLATIONS_URL_FORMAT), params());
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(violationsAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpPost);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    private String createUrl(Patchset patchset, String formatUrl) {
        return String.format(formatUrl, patchset.getProvider().getName(), patchset.getProjectPath(), patchset.getPullRequestId());
    }

    @NotNull
    private NameValuePair[] params() {
        List<NameValuePair> params = new ArrayList<>();
        if (StringUtils.isNotBlank(apiKey)) {
            params.add(new BasicNameValuePair(API_KEY_PARAM, apiKey));
        }
        if (StringUtils.isNotBlank(buildId)) {
            params.add(new BasicNameValuePair(BUILD_ID_PARAM, buildId));
        }
        return params.toArray(new NameValuePair[params.size()]);
    }
}
