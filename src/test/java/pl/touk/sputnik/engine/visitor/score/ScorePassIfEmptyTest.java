package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.review.Review;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScorePassIfEmptyTest {
    private static final Map<String, Short> PASSING_SCORE = ImmutableMap.of("Sputnik-Pass", (short) 1);
    private static final Map<String, Short> FAILING_SCORE = ImmutableMap.of("Code-Review", (short) -2);

    private Review reviewMock = mock(Review.class);

    @Test
    void shouldPassIfViolationsIsEmpty() {
        when(reviewMock.getTotalViolationCount()).thenReturn(0);

        new ScorePassIfEmpty(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(PASSING_SCORE);
    }

    @Test
    void shouldFailIfViolationsIsNotEmpty() {
        when(reviewMock.getTotalViolationCount()).thenReturn(1);

        new ScorePassIfEmpty(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(FAILING_SCORE);
    }

}