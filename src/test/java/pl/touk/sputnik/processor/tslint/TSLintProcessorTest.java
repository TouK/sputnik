package pl.touk.sputnik.processor.tslint;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import com.google.common.collect.ImmutableMap;

public class TSLintProcessorTest extends TestEnvironment {

    private TSLintProcessor processor;

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.TSLINT_SCRIPT.getKey(), "tslint",
                GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), "src/main/resources/tslint.json"));
        processor = new TSLintProcessor();
    }

    @Test
    public void shouldReturnBasicViolationsOnSimpleFunction() throws IOException {
        // given
        String jsonResponse = IOUtils.toString(getClass().getResourceAsStream("/json/tslint-results.json"));
        ReviewResult result = new ReviewResult();

        // when
        processor.addToReview(jsonResponse, result);

        // then
        assertThat(result.getViolations()).hasSize(1);

        Violation violation = result.getViolations().get(0);
        assertThat(violation.getMessage()).isEqualTo("unused variable: 'greeter'");
        assertThat(violation.getLine()).isEqualTo(10);
        assertThat(violation.getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // when
        ReviewResult reviewResult = processor.process(nonexistantReview());

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldNotModifyReviewResultWhenNoViolation() {
        // given
        ReviewResult reviewResult = new ReviewResult();
        String jsonViolations = "";

        // when
        processor.addToReview(jsonViolations, reviewResult);

        // then
        assertThat(reviewResult.getViolations().isEmpty()).isTrue();
    }
}
