package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Joiner;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ReviewInputBuilder {

    private static final String MESSAGE_SEPARATOR = ". ";

    private final CommentFilter commentFilter;

    @NotNull
    public ReviewInput toReviewInput(@NotNull Review review, @Nullable String tag) {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.message = Joiner.on(MESSAGE_SEPARATOR).join(review.getMessages());
        reviewInput.labels = new HashMap<>(review.getScores());
        if (StringUtils.isNotBlank(tag)) {
            reviewInput.tag = tag;
        }
        reviewInput.comments = review.getFiles().stream()
                .collect(Collectors.toMap(ReviewFile::getReviewFilename, this::buildFileComments));
        return reviewInput;
    }

    @NotNull
    private List<ReviewInput.CommentInput> buildFileComments(@NotNull ReviewFile reviewFile) {
        return reviewFile.getComments().stream()
                .filter(comment -> this.includes(reviewFile, comment))
                .map(this::buildCommentInput)
                .collect(Collectors.toList());
    }

    private boolean includes(@NotNull ReviewFile reviewFile, @NotNull Comment comment) {
        boolean include = commentFilter.include(reviewFile.getReviewFilename(), comment.getLine());
        if (!include) {
            log.info("Comment excluded in file {}: line {}, message {}", reviewFile.getReviewFilename(), comment.getLine(), comment.getMessage());
        }
        return include;
    }

    @NotNull
    private ReviewInput.CommentInput buildCommentInput(Comment comment) {
        ReviewInput.CommentInput commentInput = new ReviewInput.CommentInput();
        commentInput.line = comment.getLine();
        commentInput.message = comment.getMessage();
        return commentInput;
    }
}
