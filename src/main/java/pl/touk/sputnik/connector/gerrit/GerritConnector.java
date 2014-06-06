package pl.touk.sputnik.connector.gerrit;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class GerritConnector extends AbstractConnector {
    private static final String GET_LIST_FILES_URL_FORMAT = "/a/changes/%s/revisions/%s/files/";
    private static final String POST_SET_REVIEW_URL_FORMAT = "/a/changes/%s/revisions/%s/review";

    public GerritConnector(String host, int port, String username, String password, boolean useHttps) {
        super(host, port, username, password, useHttps);
    }

    @NotNull
    public String listFiles(Patchset patchset) throws URISyntaxException, IOException {
        GerritPatchset gerritPatchset = (GerritPatchset) patchset;

        URI uri = new URIBuilder().setPath(getHost() + String.format(GET_LIST_FILES_URL_FORMAT,
                gerritPatchset.getChangeId(), gerritPatchset.getRevisionId()))
                .build();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse httpResponse = logAndExecute(httpGet);
        return consumeAndLogEntity(httpResponse);
    }

    @NotNull
    public String setReview(Patchset patchset, String reviewInputAsJson) throws URISyntaxException, IOException {
        GerritPatchset gerritPatchset = (GerritPatchset) patchset;

        log.info("Setting review {}", reviewInputAsJson);
        URI uri = new URIBuilder().setPath(getHost() + String.format(POST_SET_REVIEW_URL_FORMAT,
                gerritPatchset.getChangeId(), gerritPatchset.getRevisionId()))
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(reviewInputAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = logAndExecute(httpPost);
        return consumeAndLogEntity(httpResponse);
    }
}
