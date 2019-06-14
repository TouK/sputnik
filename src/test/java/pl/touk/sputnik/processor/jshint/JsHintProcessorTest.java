package pl.touk.sputnik.processor.jshint;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;

import static pl.touk.sputnik.SputnikAssertions.assertThat;

class JsHintProcessorTest {

    @Test
    void shouldReturnEmptyResultWhenNoFilesToReview() {
        Configuration config = ConfigurationBuilder.initFromResource("jshint/sputnik/noConfigurationFile.properties");
        JsHintProcessor fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        Configuration config = ConfigurationBuilder.initFromResource("jshint/sputnik/withConfigurationFile.properties");
        JsHintProcessor fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(2);
        assertThat(reviewResult.getViolations().get(0)).hasLine(2)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(1)).hasLine(1)
                .hasMessage("'test' is defined but never used.")
                .hasSeverity(Severity.INFO);
    }

    @Test
    void shouldReturnViolationsWithConfigurationOnReducerExample() {
        Configuration config = ConfigurationBuilder.initFromResource("jshint/sputnik/withConfigurationFile.properties");
        JsHintProcessor fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/reducers.js").getFile())), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(9);
        assertThat(reviewResult.getViolations().get(0)).hasLine(4)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(1)).hasLine(10)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(2)).hasLine(11)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(3)).hasLine(13)
                .hasMessage("Regular parameters should not come after default parameters.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(4)).hasLine(16)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(5)).hasLine(18)
                .hasMessage("Missing semicolon.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(6)).hasLine(4)
                .hasMessage("'combineReducers' is defined but never used.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(7)).hasLine(6)
                .hasMessage("'ADD_TODO' is defined but never used.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(8)).hasLine(7)
                .hasMessage("'TOGGLE_TODO' is defined but never used.")
                .hasSeverity(Severity.INFO);
    }
}