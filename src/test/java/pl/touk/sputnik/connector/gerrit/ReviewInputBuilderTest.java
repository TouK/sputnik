package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.api.changes.ReviewInput.RobotCommentInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReviewInputBuilderTest {

    private static final String TAG = "ci";

    private final ReviewInputBuilder reviewInputBuilder = new ReviewInputBuilder();

    @Test
    void shouldBuildReviewInput() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = reviewInputBuilder.toReviewInput(review, TAG);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).isNull();
        assertThat(reviewInput.robotComments).hasSize(4);
        assertThat(reviewInput.tag).isEqualTo(TAG);
        List<RobotCommentInput> file1comments = reviewInput.robotComments.get("filename1");
        assertThat(file1comments).hasSize(2);
        RobotCommentInput comment1 = file1comments.get(0);
        assertThat(comment1.message).isEqualTo("test1");
        assertThat(comment1.robotId).isEqualTo("sputnik");
        assertThat(comment1.robotRunId).isNotEmpty();
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSetEmptyOrNullTag(String tag) {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = reviewInputBuilder.toReviewInput(review, tag);

        assertThat(reviewInput.tag).isNull();
    }
}