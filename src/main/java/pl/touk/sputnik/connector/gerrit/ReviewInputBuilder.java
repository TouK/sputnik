package pl.touk.sputnik.connector.gerrit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import com.google.common.base.Joiner;
import com.google.gerrit.extensions.api.changes.ReviewInput;

@Slf4j
@AllArgsConstructor
public class ReviewInputBuilder {

    private final CommentFilter commentFilter;

    @NotNull
    public ReviewInput toReviewInput(@NotNull Review review) {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.message = Joiner.on(". ").join(review.getMessages());
        reviewInput.labels = new HashMap<>(review.getScores());
        reviewInput.comments = new HashMap<>();
        for (ReviewFile file : review.getFiles()) {
            List<ReviewInput.CommentInput> comments = new ArrayList<>();
            for (Comment comment : file.getComments()) {
                if (commentFilter.filter(file.getReviewFilename(), comment.getLine())) {
                    log.debug("Comment excluded in file {} line {}", file.getReviewFilename(), comment.getLine());
                    continue;
                }
                ReviewInput.CommentInput commentInput = new ReviewInput.CommentInput();
                commentInput.line = comment.getLine();
                commentInput.message = comment.getMessage();
                comments.add(commentInput);
            }
            reviewInput.comments.put(file.getReviewFilename(), comments);
        }

        return reviewInput;
    }
}
