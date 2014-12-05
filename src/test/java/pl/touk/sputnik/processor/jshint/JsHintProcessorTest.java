package pl.touk.sputnik.processor.jshint;

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

public class JsHintProcessorTest {

    private final JsHintProcessor fixture = new JsHintProcessor();

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("jshint/sputnik/noConfigurationFile.properties");
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
    public void shouldReturnNoViolationsOnSimpleFunction() {
        // given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        ConfigurationHolder.initFromResource("jshint/sputnik/withConfigurationFile.properties");
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
