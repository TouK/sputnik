package pl.touk.sputnik.processor.pmd;

import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.processor.jslint.JsLintProcessor;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewResult;

import java.util.Collections;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

public class PmdProcessorTest extends TestEnvironment {

    private PmdProcessor fixture;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        fixture = new PmdProcessor(config);
    }

    @Test
    public void shouldReturnPmdViolations() {
        // when
        ReviewResult reviewResult = fixture.process(review());

        // then
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(4)
                .extracting("message")
                .contains("All classes and interfaces must belong to a named package")
                .contains("Each class should declare at least one constructor");
    }

    @Test
    public void shouldThrowReviewExceptionOnNotFoundFile() {
        // when
        catchException(fixture).process(nonexistantReview("NotExistingFile.java"));

        // then
        assertThat(caughtException()).isInstanceOf(ReviewException.class);
    }

    @Test
    public void shouldReturnEmptyResultWhenNoFilesToReview() {
        // given
        Review review = nonexistantReview("FileWithoutJavaExtension.txt");

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNull();
    }
}