package pl.touk.sputnik.processor.pylint;

import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class PylintProcessorTest extends TestEnvironment {

    private PylintProcessor fixture;

    @Before
    public void setUp() {
        config = ConfigurationBuilder.initFromResource("pylint/sputnik/noConfigurationFile.properties");
        fixture = new PylintProcessor(config);
    }


    @Test
    public void shouldReturnNoViolationsWhenThereIsNoFileToReview() {
        // when
        ReviewResult reviewResult = fixture.process(nonexistantReview());

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }
}