package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.connector.gerrit.json.ReviewLineComment;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.ArrayList;
import java.util.List;

public class ReviewInputBuilder {

    @NotNull
    public ReviewInput toReviewInput(@NotNull Review review) {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.message = Joiner.on(". ").join(review.getMessages());
        reviewInput.labels.putAll(review.getScores());
        for (ReviewFile file : review.getFiles()) {
            List<ReviewFileComment> comments = new ArrayList<ReviewFileComment>();
            for (Comment comment : file.getComments()) {
                comments.add(new ReviewLineComment(comment.getLine(), comment.getMessage()));
            }
            reviewInput.comments.put(file.getReviewFilename(), comments);
        }

        return reviewInput;
    }
}
