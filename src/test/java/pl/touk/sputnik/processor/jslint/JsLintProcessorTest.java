package pl.touk.sputnik.processor.jslint;

import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class JsLintProcessorTest extends TestEnvironment {

    private final JsLintProcessor fixture = new JsLintProcessor();

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
}
