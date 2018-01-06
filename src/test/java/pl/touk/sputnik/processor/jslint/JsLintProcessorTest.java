package pl.touk.sputnik.processor.jslint;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.*;

import static pl.touk.sputnik.SputnikAssertions.assertThat;

public class JsLintProcessorTest extends TestEnvironment {

    private JsLintProcessor fixture;

    @Before
    public void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/noConfigurationFile.properties");
        fixture = new JsLintProcessor(config);
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), ReviewFormatterFactory.get(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsOnSimpleFunction() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
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
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/withConfigurationFile.properties");
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
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
