package pl.touk.sputnik.connector.gerrit;

import org.junit.Test;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewInputBuilderTest {

    @Test
    public void shouldBuildReviewInput() {
        Review review = ReviewBuilder.buildReview();

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).getMessage()).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo(1);
    }

}