package pl.touk.sputnik.processor.checkstyle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

class CheckstyleProcessorTest extends TestEnvironment {

    private CheckstyleProcessor fixture;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new CheckstyleProcessor(config);
    }

    @Test
    void shouldReturnBasicSunViolationsOnSimpleClass() {
        ReviewResult reviewResult = fixture.process(review());

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
