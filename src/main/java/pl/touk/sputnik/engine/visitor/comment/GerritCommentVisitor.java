package pl.touk.sputnik.engine.visitor.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.engine.diff.FileDiff;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GerritCommentVisitor implements AfterReviewVisitor {

    private final GerritFileDiffBuilderWrapper gerritFileDiffBuilderWrapper;

    @Override
    public void afterReview(@NotNull Review review) {
        List<FileDiff> fileDiffs;
        try {
            fileDiffs = gerritFileDiffBuilderWrapper.buildFileDiffs();
        } catch (GerritException e) {
            log.error("Couldn't fetch file diffs from gerrit. All comments will be included", e);
            return;
        }

        for (ReviewFile reviewFile : review.getFiles()) {
            Iterator<Comment> iterator = reviewFile.getComments().iterator();
            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                if (!include(fileDiffs, reviewFile.getReviewFilename(), comment.getLine())) {
                    log.info("Comment excluded in file {}: line {}, message {}", reviewFile.getReviewFilename(), comment.getLine(), comment.getMessage());
                    iterator.remove();
                }
            }
        }
    }

    private boolean include(@Nonnull List<FileDiff> fileDiffs, @Nonnull String filePath, int line) {
        return fileDiffs.stream()
                .filter(fileDiff1 -> StringUtils.equals(fileDiff1.getFileName(), filePath))
                .findFirst()
                .map(fileDiff -> fileDiff.getModifiedLines().contains(line))
                .orElse(false);
    }
}
