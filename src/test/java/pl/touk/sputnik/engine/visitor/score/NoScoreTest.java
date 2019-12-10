package pl.touk.sputnik.engine.visitor.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NoScoreTest {

    @Mock
    private Review review;

    @Test
    void shouldAddNoScoreToReview() {
        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}