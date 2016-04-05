package pl.touk.sputnik.processor.tslint;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class TSLintProcessorTest extends TestEnvironment {

    private TSLintProcessor fixture;

    @Before
    public void setUp() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.TSLINT_SCRIPT.getKey(), "tslint",
                GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), "src/main/resources/tslint.json"));
        fixture = new TSLintProcessor(config);
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // when
        ReviewResult reviewResult = fixture.process(nonexistantReview());

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

}
