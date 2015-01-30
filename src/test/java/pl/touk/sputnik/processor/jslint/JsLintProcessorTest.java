package pl.touk.sputnik.processor.jslint;

import static org.assertj.core.api.Assertions.assertThat;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class JsLintProcessorTest extends TestEnvironment {

    private final JsLintProcessor fixture = new JsLintProcessor();


    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("jslint/sputnik/noConfigurationFile.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // when
        ReviewResult reviewResult = fixture.process(nonexistantReview());

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnBasicViolationsOnSimpleFunction() {
        // when
        ReviewResult reviewResult = fixture.process(review("js/test.js"));

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(2);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly(
                        "Use spaces, not tabs.",
                        "Missing 'use strict' statement."
                );
    }

    @Test
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        ConfigurationHolder.initFromResource("jslint/sputnik/withConfigurationFile.properties");
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(1);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly(
                        "'alert' is not defined."
                );
    }
}
