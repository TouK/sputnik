package pl.touk.sputnik.engine.visitor;

import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatter;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SummaryMessageVisitorTest {

    private static final String TOTAL_8_VIOLATIONS_FOUND = "Total 8 violations found";
    private static final String PROBLEM_SOURCE = "PMD";
    private static final String PROBLEM_MESSAGE = "configuration error";
    private static final String PROBLEM_FORMATTED_MESSAGE = "There is a problem with PMD: configuration error";

    @Test
    public void shouldAddSummaryMessage() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Collections.<ReviewFile>emptyList(), new ReviewFormatter(config));
        review.setTotalViolationCount(8);

        new SummaryMessageVisitor("Perfect").afterReview(review);

        assertThat(review.getMessages()).containsOnly(TOTAL_8_VIOLATIONS_FOUND);
    }

    @Test
    public void shouldAddPerfectMessageIfThereAreNoViolationsFound() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Collections.<ReviewFile>emptyList(), new ReviewFormatter(config));
        review.setTotalViolationCount(0);

        new SummaryMessageVisitor("Perfect").afterReview(review);

        assertThat(review.getMessages()).containsOnly("Perfect");
    }

    @Test
    public void shouldAddProblemMessagesPerfectMessageIfThereAreNoViolationsFound() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Collections.<ReviewFile>emptyList(), new ReviewFormatter(config));
        review.setTotalViolationCount(8);
        review.addProblem(PROBLEM_SOURCE, PROBLEM_MESSAGE);

        new SummaryMessageVisitor("Perfect").afterReview(review);

        assertThat(review.getMessages()).containsSequence(TOTAL_8_VIOLATIONS_FOUND, PROBLEM_FORMATTED_MESSAGE);
    }

}
