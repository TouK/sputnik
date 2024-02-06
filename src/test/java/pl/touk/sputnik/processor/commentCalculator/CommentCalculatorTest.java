package pl.touk.sputnik.processor.commentCalculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentCalculatorTest {
    @Test
    void shouldReturnCorrectCommentAmount() {
        // Given
        String code = "Example code";
        String commentPattern = "//";

        CommentCalculator commentCalculator = new CommentCalculator();

        // When
        int result = commentCalculator.calculateCommentAmount(code, commentPattern);

        // Then
        assertEquals(0, result);
    }
}