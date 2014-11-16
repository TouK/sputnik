package pl.touk.sputnik.connector.gerrit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.connector.gerrit.json.FileInfo;
import pl.touk.sputnik.connector.gerrit.json.ListFilesResponse;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
                FileInfo value = stringFileInfoEntry.getValue();
                if (value.getStatus() == FileInfo.Status.DELETED) {
                    continue;
                }
                files.add(new ReviewFile(stringFileInfoEntry.getKey()));
            }
            return files;
        } catch (IOException | URISyntaxException e) {
            throw new GerritException("Error when listing files", e);
        }
    }

    @Override
    public void setReview(@NotNull Review review) {
        try {
            String json = objectMapper.writeValueAsString(new ReviewInputBuilder().toReviewInput(review));
            gerritConnector.sendReview(json);
        } catch (IOException | URISyntaxException e) {
            throw new GerritException("Error when setting review", e);
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

    @Override
    public void supports(Configuration configuration) throws GeneralOptionNotSupportedException {
        boolean commentOnlyChangedLines = Boolean.parseBoolean(configuration
                .getProperty(GeneralOption.COMMENT_ONLY_CHANGED_LINES));

        if (commentOnlyChangedLines) {
            throw new GeneralOptionNotSupportedException("This connector does not support "
                    + GeneralOption.COMMENT_ONLY_CHANGED_LINES.getKey());
        }
    }
}
