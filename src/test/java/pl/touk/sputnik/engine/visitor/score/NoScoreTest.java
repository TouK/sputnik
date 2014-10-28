package pl.touk.sputnik.engine.visitor.score;

import org.junit.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class NoScoreTest {

    @Test
    public void shouldAddNoScoreToReview() {
        Review review = new Review(Collections.<ReviewFile>emptyList());

        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}