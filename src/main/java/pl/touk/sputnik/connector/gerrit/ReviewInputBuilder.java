package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Joiner;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import java.util.UUID;
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
    private static final String ROBOT_ID = "sputnik";
    private static final String MESSAGE_SEPARATOR = ". ";

    @NotNull
    public ReviewInput toReviewInput(@NotNull Review review, @Nullable String tag) {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.message = Joiner.on(MESSAGE_SEPARATOR).join(review.getMessages());
        reviewInput.labels = new HashMap<>(review.getScores());
        if (StringUtils.isNotBlank(tag)) {
            reviewInput.tag = tag;
        }

        String runId = UUID.randomUUID().toString();
        reviewInput.robotComments = review.getFiles().stream()
                .collect(Collectors.toMap(ReviewFile::getReviewFilename, reviewFile -> buildFileComments(reviewFile, runId)));
        return reviewInput;
    }

    @NotNull
    private List<ReviewInput.RobotCommentInput> buildFileComments(@NotNull ReviewFile reviewFile, String runId) {
        return reviewFile.getComments().stream()
                .map((Comment comment) -> buildCommentInput(comment, runId))
                .collect(Collectors.toList());
    }

    @NotNull
    private ReviewInput.RobotCommentInput buildCommentInput(Comment comment, String runId) {
        ReviewInput.RobotCommentInput commentInput = new ReviewInput.RobotCommentInput();

        commentInput.robotId = ROBOT_ID;
        commentInput.robotRunId = runId;
        commentInput.line = comment.getLine();
        commentInput.message = comment.getMessage();
        return commentInput;
    }
}
