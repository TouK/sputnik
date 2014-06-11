package pl.touk.sputnik.connector.gerrit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.json.ListFilesResponse;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GerritFacade implements ConnectorFacade {
    private static final String CONNECTOR_NAME = "gerrit";
    private static final String RESPONSE_PREFIX = ")]}'";
    private static final String COMMIT_MSG = "/COMMIT_MSG";
    public static final String GERRIT_HOST = "gerrit.host";
    public static final String GERRIT_PORT = "gerrit.port";
    public static final String GERRIT_USE_HTTPS = "gerrit.useHttps";
    public static final String GERRIT_USERNAME = "gerrit.username";
    public static final String GERRIT_PASSWORD = "gerrit.password";
    private GerritConnector gerritConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public GerritFacade(@NotNull CloseableHttpClient httpClient, @NotNull HttpClientContext httpClientContext, @NotNull GerritPatchset gerritPatchset) {
        gerritConnector = new GerritConnector(new HttpConnector(httpClient, httpClientContext), gerritPatchset);
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            String response = gerritConnector.listFiles();
            String jsonString = trimResponse(response);
            ListFilesResponse listFilesResponse = objectMapper.readValue(jsonString, ListFilesResponse.class);

            List<ReviewFile> files = new ArrayList<ReviewFile>();
            Set<String> keys = listFilesResponse.keySet();
            keys.remove(COMMIT_MSG);
            for (String key : keys) {
                files.add(new ReviewFile(key));
            }
            return files;
        } catch (IOException | URISyntaxException e) {
            throw new GerritException("Error listing files", e);
        }
    }

    @Override
    public void setReview(@NotNull ReviewInput reviewInput) {
        try {
            String json = objectMapper.writeValueAsString(reviewInput);
            gerritConnector.sendReview(json);
        } catch (IOException | URISyntaxException e) {
            throw new GerritException("Error setting review", e);
        }
    }

    @NotNull
    protected String trimResponse(@NotNull String response) {
        return StringUtils.replaceOnce(response, RESPONSE_PREFIX, "");
    }

    @Override
    public String name() {
        return CONNECTOR_NAME;
    }
}
