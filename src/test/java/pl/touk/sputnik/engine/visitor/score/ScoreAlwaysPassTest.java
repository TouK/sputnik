package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ScoreAlwaysPassTest {

    @Test
    public void shouldAddScoreToReview() {
        Review review = new Review(Collections.<ReviewFile>emptyList());

        new ScoreAlwaysPass(ImmutableMap.of("Sputnik-Pass", 1)).afterReview(review);

        assertThat(review.getScores()).containsOnly(entry("Sputnik-Pass", 1));
    }

}