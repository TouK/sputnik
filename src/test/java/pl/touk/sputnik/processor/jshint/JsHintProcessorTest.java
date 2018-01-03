package pl.touk.sputnik.processor.jshint;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

public class JsHintProcessorTest {

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Configuration config = ConfigurationBuilder.initFromResource("jshint/sputnik/noConfigurationFile.properties");
        JsHintProcessor fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), ReviewFormatterFactory.get(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnOneViolationWithConfigurationOnSimpleFunction() {
        // given
        Configuration config = ConfigurationBuilder.initFromResource("jshint/sputnik/withConfigurationFile.properties");
        JsHintProcessor fixture = new JsHintProcessor(config);
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("js/test.js").getFile())), ReviewFormatterFactory.get(config));

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(1);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly("Missing semicolon.");
    }
}
