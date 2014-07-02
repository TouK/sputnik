package pl.touk.sputnik.connector.gerrit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.json.FileInfo;
import pl.touk.sputnik.connector.gerrit.json.ListFilesResponse;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.touk.sputnik.Connectors;

public class GerritFacade implements ConnectorFacade {
    private static final String RESPONSE_PREFIX = ")]}'";
    private static final String COMMIT_MSG = "/COMMIT_MSG";
    private GerritConnector gerritConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public GerritFacade(GerritConnector gerritConnector) {
        this.gerritConnector = gerritConnector;
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            String response = gerritConnector.listFiles();
            String jsonString = trimResponse(response);
            ListFilesResponse listFilesResponse = objectMapper.readValue(jsonString, ListFilesResponse.class);

            List<ReviewFile> files = new ArrayList<ReviewFile>();
            for (Map.Entry<String, FileInfo> stringFileInfoEntry : listFilesResponse.entrySet()) {
                if (COMMIT_MSG.equals(stringFileInfoEntry.getKey())) {
                    continue;
                }
                files.add(new ReviewFile(stringFileInfoEntry.getKey(), stringFileInfoEntry.getValue().getStatus().getModificationType()));
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
    public Connectors name() {
        return Connectors.GERRIT;
    }
}
