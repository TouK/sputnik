package pl.touk.sputnik.connector.gerrit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;

@Slf4j
@AllArgsConstructor
public class GerritFacade implements ConnectorFacade {
    private static final String COMMIT_MSG = "/COMMIT_MSG";

    private final GerritApi gerritApi;

    private final GerritPatchset gerritPatchset;

    private final CommentFilter commentFilter;

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            List<ReviewFile> files = new ArrayList<ReviewFile>();
            Map<String, FileInfo> changeFiles = gerritApi.changes().id(gerritPatchset.getChangeId())
                    .revision(gerritPatchset.getRevisionId()).files();

            for (Map.Entry<String, FileInfo> changeFileEntry : changeFiles.entrySet()) {
                if (COMMIT_MSG.equals(changeFileEntry.getKey())) {
                    continue;
                }
                FileInfo fileInfo = changeFileEntry.getValue();
                if (isDeleted(fileInfo)) {
                    continue;
                }
                files.add(new ReviewFile(changeFileEntry.getKey()));
            }
            return files;
        } catch (RestApiException e) {
            throw new GerritException("Error when listing files", e);
        }
    }

    private boolean isDeleted(FileInfo fileInfo) {
        return fileInfo.status != null && fileInfo.status == 'D';
    }

    @Override
    public void setReview(@NotNull Review review) {
        try {
            log.debug("Set review in Gerrit: {}", review);
            ReviewInput reviewInput = new ReviewInputBuilder(commentFilter.init()).toReviewInput(review);
            gerritApi.changes().id(gerritPatchset.getChangeId()).revision(gerritPatchset.getRevisionId()).review(reviewInput);
        } catch (RestApiException e) {
            throw new GerritException("Error when setting review", e);
        }
    }

    @Override
    public Connectors name() {
        return Connectors.GERRIT;
    }

    @Override
    public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {
    }
}
