package pl.touk.sputnik.engine.visitor.comment;

import com.google.gerrit.extensions.api.changes.FileApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.engine.diff.FileDiff;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GerritCommentVisitor implements AfterReviewVisitor {

    private final GerritFacade gerritFacade;

    private Map<String, FileDiff> modifiedLines;

    @Override
    public void afterReview(@NotNull Review review) {
        init();

        for (ReviewFile reviewFile : review.getFiles()) {
            Iterator<Comment> iterator = reviewFile.getComments().iterator();
            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                if (!include(reviewFile.getReviewFilename(), comment.getLine())) {
                    log.info("Comment excluded in file {}: line {}, message {}", reviewFile.getReviewFilename(), comment.getLine(), comment.getMessage());
                    iterator.remove();
                }
            }
        }
    }


    private void init() {
        log.info("Query all file diffs to only include comments on modified lines");
        modifiedLines = new HashMap<>();
        GerritFileDiffBuilder gerritFileDiffBuilder = new GerritFileDiffBuilder();
        try {
            RevisionApi revision = gerritFacade.getRevision();

            for (Map.Entry<String, FileInfo> file : revision.files().entrySet()) {
                log.info("Query file diff for {}", file.getKey());
                FileApi fileApi = revision.file(file.getKey());
                FileDiff fileDiff = gerritFileDiffBuilder.build(file.getKey(), fileApi.diff().content);
                modifiedLines.put(fileDiff.getFileName(), fileDiff);
                log.info("Query file diff for {} finished", file.getKey());
            }
        } catch (RestApiException e) {
            throw new GerritException("Error when retrieve modified lines", e);
        }
        log.info("Query all file diffs finished");
    }

    private boolean include(String filePath, int line) {
        FileDiff diff = modifiedLines == null ? null : modifiedLines.get(filePath);
        return diff != null && diff.getModifiedLines().contains(line);
    }
}
