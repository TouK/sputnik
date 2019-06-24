package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewInputBuilderTest {

    private static final String TAG = "ci";

    @Mock
    private CommentFilter commentFilter;

    private ReviewInputBuilder reviewInputBuilder;

    @BeforeEach
    void setUp() {
        reviewInputBuilder = new ReviewInputBuilder(commentFilter);
    }


    @Test
    void shouldBuildReviewInput() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);
        when(commentFilter.include(eq("filename1"), anyInt())).thenReturn(true);

        ReviewInput reviewInput = reviewInputBuilder.toReviewInput(review, TAG);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.tag).isEqualTo(TAG);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

    @Test
    void shouldBuildReviewInputWithCommentFilter() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);
        when(commentFilter.include("filename1", 0)).thenReturn(false);
        when(commentFilter.include("filename1", 1)).thenReturn(true);

        ReviewInput reviewInput = reviewInputBuilder.toReviewInput(review, TAG);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.comments.get("filename1")).hasSize(1);
        assertThat(reviewInput.comments.get("filename1").get(0).line).isEqualTo(1);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test2");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

    @Test
    void shouldNotSetEmptyTag() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review, "");

        assertThat(reviewInput.tag).isNull();
    }

    @Test
    void shouldNotSetNullTag() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review, null);

        assertThat(reviewInput.tag).isNull();
    }

}