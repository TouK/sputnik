package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.Test;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewInputBuilderTest {

    @Test
    public void shouldBuildReviewInput() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

}