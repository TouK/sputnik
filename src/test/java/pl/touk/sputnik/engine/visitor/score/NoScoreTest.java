package pl.touk.sputnik.engine.visitor.score;

import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

public class NoScoreTest extends TestEnvironment {

    @Test
    public void shouldAddNoScoreToReview() {
        Review review = review();

        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}