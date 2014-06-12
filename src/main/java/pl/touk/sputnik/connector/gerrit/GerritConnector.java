package pl.touk.sputnik.connector.gerrit;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.Connector;
import pl.touk.sputnik.connector.http.HttpConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@AllArgsConstructor
public class GerritConnector implements Connector {

    protected static final String GET_LIST_FILES_URL_FORMAT = "/a/changes/%s/revisions/%s/files/";
    protected static final String POST_SET_REVIEW_URL_FORMAT = "/a/changes/%s/revisions/%s/review";

    private HttpConnector httpConnector;
    private GerritPatchset gerritPatchset;

    @NotNull
    public String listFiles() throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(gerritPatchset, GET_LIST_FILES_URL_FORMAT));
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpGet);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }


    @NotNull
    public String sendReview(String reviewInputAsJson) throws URISyntaxException, IOException {
        log.info("Setting review {}", reviewInputAsJson);
        URI uri = httpConnector.buildUri(createUrl(gerritPatchset, POST_SET_REVIEW_URL_FORMAT));
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(reviewInputAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpPost);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    @VisibleForTesting
    static String createUrl(GerritPatchset gerritPatchset, String urlFormat) {
        return String.format(urlFormat,
                gerritPatchset.getChangeId(), gerritPatchset.getRevisionId());
    }

}
