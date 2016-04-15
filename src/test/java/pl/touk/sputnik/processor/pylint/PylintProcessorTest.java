package pl.touk.sputnik.processor.pylint;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class PylintProcessorTest extends TestEnvironment {

    private PylintProcessor fixture;

    @Before
    public void setUp() {
        initProcessorWithConfig("pylint/sputnik/noConfigurationFile.properties");
    }

    @Test
    public void shouldReturnViolations() {
        // given
        Review review = createReviewForFile("pylint/PythonTest.py");

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isNotEmpty();
    }

    @Test
    public void shouldReturnNoViolationsIfFileIsCorrect() {
        // given
        Review review = createReviewForFile("pylint/PythonCorrect.py");

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldUseRcfileWithConfiguration() {
        // given
        initProcessorWithConfig("pylint/sputnik/withConfigurationFile.properties");
        Review review = createReviewForFile("pylint/PythonTest.py");

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isNotEmpty();
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .doesNotContain("Black listed name \"bar\" [blacklisted-name]")
                .doesNotContain("Black listed name \"foo\" [blacklisted-name]");
    }

    private void initProcessorWithConfig(String configFilePath) {
        config = ConfigurationBuilder.initFromResource(configFilePath);
        fixture = new PylintProcessor(config);
    }

    private Review createReviewForFile(String filePath) {
        return new Review(ImmutableList.of(new ReviewFile(Resources.getResource(filePath).getFile())), ReviewFormatterFactory.get(config));
    }
}