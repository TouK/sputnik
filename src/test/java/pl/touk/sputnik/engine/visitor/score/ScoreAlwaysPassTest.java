package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(MockitoExtension.class)
class ScoreAlwaysPassTest {

    private Review review;

    @Mock
    private List<ReviewFile> files;

    @Mock
    private ReviewFormatter reviewFormatter;

    @BeforeEach
    void setUp() {
        review = new Review(files, reviewFormatter);
    }

    @Test
    void shouldAddScoreToReview() {
        new ScoreAlwaysPass(ImmutableMap.of("Sputnik-Pass", (short) 1)).afterReview(review);

        assertThat(review.getScores()).containsOnly(entry("Sputnik-Pass", (short) 1));
    }

}