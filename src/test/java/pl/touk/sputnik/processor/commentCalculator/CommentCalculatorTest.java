package pl.touk.sputnik.processor.commentCalculator;


import java.util.List;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.GroovyFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.Severity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentCalculatorTest {

    @Test
    void shouldReturnNoCommentsForFile() {
        // Create a mock review object
        Review review = mock(Review.class);
        List<String> reviewFiles = Collections.singletonList("TestFile.groovy");
        when(review.getFiles(new GroovyFilter(), new FileNameTransformer())).thenReturn(reviewFiles);

        // Create an instance of CommentCalculator
        CommentCalculator commentCalculator = new CommentCalculator();

        // Call the process method and get the result
        ReviewResult result = commentCalculator.process(review);

        // Verify the result
        assertEquals(1, result.getViolations().size());
        assertEquals("Comment amount: 0", result.getViolations().get(0).getMessage());
        assertEquals(Severity.INFO, result.getViolations().get(0).getSeverity());
    }
}