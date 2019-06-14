package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ScoreAlwaysPassTest extends TestEnvironment {

    @Test
    void shouldAddScoreToReview() {
        Review review = review();

        new ScoreAlwaysPass(ImmutableMap.of("Sputnik-Pass", (short) 1)).afterReview(review);

        assertThat(review.getScores()).containsOnly(entry("Sputnik-Pass", (short) 1));
    }

}