package pl.touk.sputnik.processor.jslint;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;

import static pl.touk.sputnik.SputnikAssertions.assertThat;

class JsLintProcessorTest extends TestEnvironment {

    private JsLintProcessor fixture;

    @BeforeEach
    void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/noConfigurationFile.properties");
        fixture = new JsLintProcessor(config);
    }

    @Test
    void shouldReturnEmptyResultWhenNoFilesToReview() {
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnNoViolationsOnSimpleFunction() {
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(2);
        assertThat(reviewResult.getViolations().get(0)).hasLine(2)
                .hasMessage("Missing 'use strict' statement.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(1)).hasLine(2)
                .hasMessage("Expected ';' and instead saw '}'.")
                .hasSeverity(Severity.INFO);
    }

    @Test
    void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/withConfigurationFile.properties");
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = fixture.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(2);
        assertThat(reviewResult.getViolations().get(0)).hasLine(2)
                .hasMessage("Missing 'use strict' statement.")
                .hasSeverity(Severity.INFO);
        assertThat(reviewResult.getViolations().get(1)).hasLine(2)
                .hasMessage("Expected ';' and instead saw '}'.")
                .hasSeverity(Severity.INFO);
    }
}
