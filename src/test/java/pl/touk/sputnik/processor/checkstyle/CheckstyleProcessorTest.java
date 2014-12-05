package pl.touk.sputnik.processor.checkstyle;

import org.junit.Test;
import org.mockito.Mock;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckstyleProcessorTest extends TestEnvironment {

    private final CheckstyleProcessor fixture = new CheckstyleProcessor();

    @Mock
    private Review review;

    @Test
    public void shouldReturnBasicSunViolationsOnSimpleClass() {
        //when
        ReviewResult reviewResult = fixture.process(review());

        //then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(3)
                .extracting("message")
                .containsOnly(
                        "Missing package-info.java file.",
                        "Missing a Javadoc comment."
                );
    }

}
