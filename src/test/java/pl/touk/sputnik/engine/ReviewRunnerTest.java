package pl.touk.sputnik.engine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewRunnerTest {
    private static final String PROCESSOR_SOURCE_NAME = "Test";

    @Mock
    private Review reviewMock;

    @Mock
    private ReviewResult reviewResultMock;

    @Mock
    private ReviewProcessor reviewProcessorMock;

    @Mock
    private Configuration config;

    @InjectMocks
    private ReviewRunner reviewRunner;

    @Before
    public void setUp() {
        when(reviewProcessorMock.getName()).thenReturn(PROCESSOR_SOURCE_NAME);
    }

    @Test
    public void shouldAddReviewResult() {
        when(reviewProcessorMock.process(reviewMock)).thenReturn(reviewResultMock);

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).add(PROCESSOR_SOURCE_NAME, reviewResultMock);
    }

    @Test
    public void shouldNotAddNullReview() {
        when(reviewProcessorMock.process(reviewMock)).thenReturn(null);

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock, never()).add(eq(PROCESSOR_SOURCE_NAME), any(ReviewResult.class));
    }

    @Test
    public void shouldAddReviewExceptionMessageAsAProblem() {
        when(reviewProcessorMock.process(reviewMock)).thenThrow(new ReviewException("Exception message"));

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).addProblem(PROCESSOR_SOURCE_NAME, "ReviewException: Exception message");
    }

    @Test
    public void shouldReviewExceptionCauseMessageAsAProblem() {
        IOException cause = new IOException("File not found exception");
        when(reviewProcessorMock.process(reviewMock)).thenThrow(new ReviewException("Exception message", cause));

        reviewRunner.review(reviewProcessorMock);

        verify(reviewMock).addProblem(PROCESSOR_SOURCE_NAME, "IOException: File not found exception");
    }
}