package pl.touk.sputnik.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewRunnerTest {

    private static final String PROCESSOR_SOURCE_NAME = "Test";

    @Mock
    private Review reviewMock;

    @Mock
    private ReviewResult reviewResultMock;

    @Mock
    private ReviewProcessor reviewProcessorMock;

    @Mock
    private Configuration config;

    private ReviewRunner reviewRunner;

    @BeforeEach
    void setUp() {
        when(reviewProcessorMock.getName()).thenReturn(PROCESSOR_SOURCE_NAME);
        reviewRunner = new ReviewRunner(reviewMock);
    }

    @Test
    void shouldAddReviewResult() {
        when(reviewProcessorMock.process(reviewMock)).thenReturn(reviewResultMock);

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).add(PROCESSOR_SOURCE_NAME, reviewResultMock);
    }

    @Test
    void shouldNotAddNullReview() {
        when(reviewProcessorMock.process(reviewMock)).thenReturn(null);

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock, never()).add(eq(PROCESSOR_SOURCE_NAME), any(ReviewResult.class));
    }

    @Test
    void shouldAddReviewExceptionMessageAsAProblem() {
        when(reviewProcessorMock.process(reviewMock)).thenThrow(new ReviewException("Exception message"));

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).addProblem(PROCESSOR_SOURCE_NAME, "ReviewException: Exception message");
    }

    @Test
    void shouldReviewExceptionCauseMessageAsAProblem() {
        IOException cause = new IOException("File not found exception");
        when(reviewProcessorMock.process(reviewMock)).thenThrow(new ReviewException("Exception message", cause));

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).addProblem(PROCESSOR_SOURCE_NAME, "IOException: File not found exception");
    }
}