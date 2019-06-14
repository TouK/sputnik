package pl.touk.sputnik.processor.pmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PmdProcessorTest extends TestEnvironment {

    private PmdProcessor fixture;

    @BeforeEach
    void setUp() {
        fixture = new PmdProcessor(config);
    }

    @Test
    void shouldReturnPmdViolations() {
        ReviewResult reviewResult = fixture.process(review());

        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(4)
                .extracting("message")
                .contains("All classes and interfaces must belong to a named package")
                .contains("Each class should declare at least one constructor");
    }

    @Test
    void shouldThrowReviewExceptionOnNotFoundFile() {
        Throwable thrown = catchThrowable(() -> fixture.process(nonExistentReview("NotExistingFile.java")));

        assertThat(thrown).isInstanceOf(ReviewException.class);
    }

    @Test
    void shouldReturnEmptyResultWhenNoFilesToReview() {
        Review review = nonExistentReview("FileWithoutJavaExtension.txt");

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNull();
    }
}