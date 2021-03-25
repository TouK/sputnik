package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

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
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.tag).isEqualTo(TAG);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
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