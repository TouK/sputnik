package pl.touk.sputnik.engine.visitor.score;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

class NoScoreTest extends TestEnvironment {

    @Test
    void shouldAddNoScoreToReview() {
        Review review = review();

        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}