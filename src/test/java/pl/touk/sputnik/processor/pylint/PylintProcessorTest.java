package pl.touk.sputnik.processor.pylint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

class PylintProcessorTest extends TestEnvironment {

    private PylintProcessor fixture;

    @BeforeEach
    public void setUp() {
        config = ConfigurationBuilder.initFromResource("pylint/sputnik/noConfigurationFile.properties");
        fixture = new PylintProcessor(config);
    }

    @Test
    void shouldReturnNoViolationsWhenThereIsNoFileToReview() {
        ReviewResult reviewResult = fixture.process(nonExistentReview());

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }
}