package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ScoreAlwaysPassTest {

    @Test
    public void shouldAddScoreToReview() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Collections.<ReviewFile>emptyList(), config);

        new ScoreAlwaysPass(ImmutableMap.of("Sputnik-Pass", (short) 1)).afterReview(review);

        assertThat(review.getScores()).containsOnly(entry("Sputnik-Pass", (short) 1));
    }

}