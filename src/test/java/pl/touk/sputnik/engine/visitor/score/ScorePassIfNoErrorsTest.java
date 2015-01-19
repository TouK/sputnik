package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.Severity;

import java.util.Map;

import static org.mockito.Mockito.*;

public class ScorePassIfNoErrorsTest {
    private static final Map<String, Short> PASSING_SCORE = ImmutableMap.of("Sputnik-Pass", (short) 1);
    private static final Map<String, Short> FAILING_SCORE = ImmutableMap.of("Code-Review", (short) -2);

    private Review reviewMock = mock(Review.class, RETURNS_DEEP_STUBS);

    @Test
    public void shouldPassIfErrorCountIsNull() {
        when(reviewMock.getViolationCount().get(Severity.ERROR)).thenReturn(null);

        new ScorePassIfNoErrors(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(PASSING_SCORE);
    }

    @Test
    public void shouldPassIfErrorCountIsZero() {
        when(reviewMock.getViolationCount().get(Severity.ERROR)).thenReturn(0);

        new ScorePassIfNoErrors(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(PASSING_SCORE);
    }

    @Test
    public void shouldFailIfErrorCountIsNotZero() {
        when(reviewMock.getViolationCount().get(Severity.ERROR)).thenReturn(1);

        new ScorePassIfNoErrors(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(FAILING_SCORE);
    }

}