package pl.touk.sputnik.processor.scalastyle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

class ScalastyleProcessorTest extends TestEnvironment {

    private ScalastyleProcessor fixture;

    @BeforeEach
    void setUp() {
        fixture = new ScalastyleProcessor(config);
    }

    @Test
    void shouldReturnScalastyleViolations() {
        ReviewResult reviewResult = fixture.process(review("scala/Point.scala"));

        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(1)
                .extracting("message")
                .contains("Header does not match expected text");
    }

}