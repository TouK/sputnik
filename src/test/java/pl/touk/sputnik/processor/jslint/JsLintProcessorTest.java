package pl.touk.sputnik.processor.jslint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class JsLintProcessorTest {

    private final JsLintProcessor fixture = new JsLintProcessor();

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("test.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }


    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnBasicViolationsOnSimpleFunction() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())));

        // when
        ReviewResult reviewResult = fixture.process(review);

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
