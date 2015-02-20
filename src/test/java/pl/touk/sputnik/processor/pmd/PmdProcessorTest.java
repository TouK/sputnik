package pl.touk.sputnik.processor.pmd;

import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.touk.sputnik.CatchException.catchException;

public class PmdProcessorTest extends TestEnvironment {

    private final PmdProcessor fixture = new PmdProcessor();

    @Test
    public void shouldReturnPmdViolations() {
        // when
        ReviewResult reviewResult = fixture.process(review());

        // then
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(4)
                .extracting("message")
                .contains("All classes and interfaces must belong to a named package")
                .contains("Each class should declare at least one constructor");
    }

    @Test
    public void shouldThrowReviewExceptionOnNotFoundFile() {
        // when
        catchException(() -> fixture.process(nonexistantReview()), (caughtException) ->

        // then
        assertThat(caughtException).isInstanceOf(ReviewException.class));
    }

}