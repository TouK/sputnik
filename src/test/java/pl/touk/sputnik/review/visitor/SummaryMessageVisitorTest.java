package pl.touk.sputnik.review.visitor;

import org.junit.Test;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SummaryMessageVisitorTest {

    @Test
    public void shouldAddSummaryMessage() {
        Review review = new Review(Collections.<ReviewFile>emptyList());
        review.setTotalViolationsCount(8);

        new SummaryMessageVisitor().afterReview(review);

        assertThat(review.getMessages()).containsOnly("Total 8 violations found");
    }

}