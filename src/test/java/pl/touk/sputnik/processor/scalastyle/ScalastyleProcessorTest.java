package pl.touk.sputnik.processor.scalastyle;

import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.ReviewResult;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ScalastyleProcessorTest extends TestEnvironment {

    private final ScalastyleProcessor fixture = new ScalastyleProcessor();

    @Test
    public void shouldReturnScalastyleViolations() {
        // when
        ReviewResult reviewResult = fixture.process(review("scala/Point.scala"), config);

        // then
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(1)
                .extracting("message")
                .contains("Header does not match expected text");
    }

}