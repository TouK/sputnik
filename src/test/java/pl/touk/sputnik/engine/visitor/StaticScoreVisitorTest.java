package pl.touk.sputnik.engine.visitor;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class StaticScoreVisitorTest {

    @Test
    public void shouldAddScoreToReview() {
        Review review = new Review(Collections.<ReviewFile>emptyList());

        new StaticScoreVisitor(ImmutableMap.of("Verified", 1)).afterReview(review);

        assertThat(review.getScores()).containsOnly(entry("Verified", 1));
    }

}