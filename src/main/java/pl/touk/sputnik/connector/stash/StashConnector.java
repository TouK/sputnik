package pl.touk.sputnik.connector.stash;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.AbstractConnector;
import pl.touk.sputnik.Patchset;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StashConnector extends AbstractConnector {

    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/changes";
    public static final String CHANGES_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes";
    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments";
    public static final String COMMENTS_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments";

    public StashConnector(String host, int port, String username, String password, boolean useHttps) {
        super(host, port, username, password, useHttps);
    }

    @NotNull
    @Override
    public String listFiles(Patchset patchset) throws URISyntaxException, IOException {
        StashPatchset stashPatchset = (StashPatchset) patchset;
        URI uri = new URIBuilder().setPath(getHost() + createUrl(stashPatchset, CHANGES_URL_FORMAT)).build();
        HttpGet httpGet = new HttpGet(uri);
        addBasicAuthHeader(httpGet);
        CloseableHttpResponse httpResponse = logAndExecute(httpGet);
        return consumeAndLogEntity(httpResponse);
    }

    @NotNull
    @Override
    public String setReview(Patchset patchset, String reviewInputAsJson) throws URISyntaxException, IOException {
        StashPatchset stashPatchset = (StashPatchset) patchset;
        URI uri = new URIBuilder().setPath(getHost() + createUrl(stashPatchset, COMMENTS_URL_FORMAT)).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(reviewInputAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = logAndExecute(httpPost);
        return consumeAndLogEntity(httpResponse);
    }

    public static String createUrl(StashPatchset stashPatchset, String formatUrl) {
        return String.format(formatUrl,
                stashPatchset.getProjectKey(), stashPatchset.getRepositorySlug(), stashPatchset.getPullRequestId());
    }
}
