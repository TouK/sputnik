package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.Connector;
import pl.touk.sputnik.connector.http.HttpConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@AllArgsConstructor
public class StashConnector implements Connector {

    private HttpConnector httpConnector;
    private StashPatchset stashPatchset;

    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/changes";
    public static final String CHANGES_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes";
    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments";
    public static final String COMMENTS_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments";

    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/diff"
    public static final String DIFF_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff";

    @NotNull
    @Override
    public String listFiles() throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(stashPatchset, CHANGES_URL_FORMAT));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(new HttpGet(uri));
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    @NotNull
    @Override
    public String sendReview(String reviewInputAsJson) throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(stashPatchset, COMMENTS_URL_FORMAT));
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(reviewInputAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpPost);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    public String getDiffByLine(String filename) throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(stashPatchset, DIFF_URL_FORMAT) + "/" + filename,
                new BasicNameValuePair("contextLines", "-1"),
                new BasicNameValuePair("srcPath", filename),
                new BasicNameValuePair("withComments", "true"));
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpGet);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    private String createUrl(StashPatchset stashPatchset, String formatUrl) {
        return String.format(formatUrl,
                stashPatchset.getProjectKey(), stashPatchset.getRepositorySlug(), stashPatchset.getPullRequestId());
    }

}
