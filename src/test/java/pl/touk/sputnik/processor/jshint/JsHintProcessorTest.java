package pl.touk.sputnik.processor.jshint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatter;
import pl.touk.sputnik.review.ReviewResult;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class JsHintProcessorTest {

    private JsHintProcessor fixture;
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource("jshint/sputnik/noConfigurationFile.properties");
        fixture = new JsHintProcessor(config);
    }


    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), new ReviewFormatter(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsOnSimpleFunction() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), new ReviewFormatter(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        config = ConfigurationBuilder.initFromResource("jshint/sputnik/withConfigurationFile.properties");
        fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), new ReviewFormatter(config));

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
