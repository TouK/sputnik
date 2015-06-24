package pl.touk.sputnik.processor.jslint;

import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import static org.assertj.core.api.Assertions.assertThat;

public class JsLintProcessorTest extends TestEnvironment {

    private final JsLintProcessor fixture = new JsLintProcessor();

    @Before
    public void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/noConfigurationFile.properties");
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), config);

        // when
        ReviewResult reviewResult = fixture.process(review, config);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsOnSimpleFunction() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), config);

        // when
        ReviewResult reviewResult = fixture.process(review, config);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(1);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly(
                        "Missing 'use strict' statement."
                );
    }

    @Test
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        config = ConfigurationBuilder.initFromResource("jslint/sputnik/withConfigurationFile.properties");
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), config);

        // when
        ReviewResult reviewResult = fixture.process(review, config);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(1);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly(
                        "Missing 'use strict' statement."
                );
    }
}
