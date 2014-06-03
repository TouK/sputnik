package pl.touk.sputnik.stash;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.AbstractConnector;
import pl.touk.sputnik.Patchset;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StashConnector extends AbstractConnector {

    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/changes";
    private static final String CHANGES_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes";
    // "/rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments";
    private static final String COMMENTS_URL_FORMAT = "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments";

    public StashConnector(String host, int port, String username, String password) {
        super(host, port, username, password);
    }

    @NotNull
    @Override
    public String listFiles(Patchset patchset) throws URISyntaxException, IOException {
        StashPatchset stashPatchset = (StashPatchset) patchset;
        URI uri = new URIBuilder().setPath(createChangesUrl(stashPatchset)).build();
        HttpGet httpGet = new HttpGet(uri);
        addBasicAuthHeader(httpGet);
        CloseableHttpResponse httpResponse = logAndExecute(httpGet);
        return consumeAndLogEntity(httpResponse);
    }

    /*
    {
         "text": "A pithy comment on a particular line within a file.",
         "anchor": {
             "line": 1,
             "lineType": "CONTEXT",
             "fileType": "FROM"
             "path": "path/to/file",
             "srcPath": "path/to/file"
         }
     }
     */
    @NotNull
    @Override
    public String setReview(Patchset patchset, String reviewInputAsJson) throws URISyntaxException, IOException {
        return null;
    }

    public static String createChangesUrl(StashPatchset stashPatchset) {
        return String.format(CHANGES_URL_FORMAT,
                stashPatchset.getProjectKey(), stashPatchset.getRepositorySlug(), stashPatchset.getPullRequestId());
    }
}
