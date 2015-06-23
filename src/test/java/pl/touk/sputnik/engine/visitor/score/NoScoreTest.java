package pl.touk.sputnik.engine.visitor.score;

import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class NoScoreTest {

    @Test
    public void shouldAddNoScoreToReview() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Collections.<ReviewFile>emptyList(), config);

        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}