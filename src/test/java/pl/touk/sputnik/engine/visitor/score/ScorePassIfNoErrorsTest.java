package pl.touk.sputnik.engine.visitor.score;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.Severity;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScorePassIfNoErrorsTest {
    private static final Map<String, Short> PASSING_SCORE = ImmutableMap.of("Sputnik-Pass", (short) 1);
    private static final Map<String, Short> FAILING_SCORE = ImmutableMap.of("Code-Review", (short) -2);

    @Mock
    private Review reviewMock;

    @Test
    void shouldPassIfErrorCountIsZero() {
        when(reviewMock.getViolationCount(Severity.ERROR)).thenReturn(0L);

        new ScorePassIfNoErrors(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(PASSING_SCORE);
    }

    @Test
    void shouldFailIfErrorCountIsNotZero() {
        when(reviewMock.getViolationCount(Severity.ERROR)).thenReturn(1L);

        new ScorePassIfNoErrors(PASSING_SCORE, FAILING_SCORE).afterReview(reviewMock);

        verify(reviewMock).setScores(FAILING_SCORE);
    }

}