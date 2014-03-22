package pl.touk.sputnik.gerrit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.gerrit.json.ListFilesResponse;
import pl.touk.sputnik.gerrit.json.ReviewInput;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GerritFacade {
    private static final String RESPONSE_PREFIX = ")]}'";
    private static final String COMMIT_MSG = "/COMMIT_MSG";
    private static final String MAVEN_ENTRY_REGEX = ".*src/(main|test)/java/";
    private static final String DOT = ".";
    public static final String GERRIT_HOST = "gerrit.host";
    public static final String GERRIT_PORT = "gerrit.port";
    public static final String GERRIT_USERNAME = "gerrit.username";
    public static final String GERRIT_PASSWORD = "gerrit.password";
    private GerritConnector gerritConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public GerritFacade(@NotNull String host, int port, @NotNull String username, @NotNull String password) {
        gerritConnector = new GerritConnector(host, port, username, password);
    }

    /**
     * @return sonarLongName to gerritFileName map
     */
    @NotNull
    public List<ReviewFile> listFiles(@NotNull GerritPatchset gerritPatchset) {
        try {
            String response = gerritConnector.listFiles(gerritPatchset.getChangeId(), gerritPatchset.getRevisionId());
            String jsonString = trimResponse(response);
            ListFilesResponse listFilesResponse = objectMapper.readValue(jsonString, ListFilesResponse.class);

            List<ReviewFile> files = new ArrayList<ReviewFile>();
            Set<String> keys = listFilesResponse.keySet();
            keys.remove(COMMIT_MSG);
            for (String key : keys) {
                files.add(new ReviewFile(key));
            }
            return files;
        } catch (IOException e) {
            throw new GerritException("Error listing files", e);
        } catch (URISyntaxException e) {
            throw new GerritException("Error listing files", e);
        }
    }

    public void setReview(@NotNull GerritPatchset gerritPatchset, @NotNull ReviewInput reviewInput) {
        try {
            String json = objectMapper.writeValueAsString(reviewInput);
            gerritConnector.setReview(gerritPatchset.getChangeId(), gerritPatchset.getRevisionId(), json);
        } catch (JsonProcessingException e) {
            throw new GerritException("Error setting review", e);
        } catch (IOException e) {
            throw new GerritException("Error setting review", e);
        } catch (URISyntaxException e) {
            throw new GerritException("Error setting review", e);
        }
    }

    @NotNull
    protected String trimResponse(@NotNull String response) {
        return StringUtils.replaceOnce(response, RESPONSE_PREFIX, "");
    }

    void setGerritConnector(@NotNull GerritConnector gerritConnector) {
        this.gerritConnector = gerritConnector;
    }
}
