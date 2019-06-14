package pl.touk.sputnik.engine.visitor;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Review;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class LimitCommentVisitorTest {

    @Test
    void shouldNotLimitCommentsIfCountIsBelowMaximumCount() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = ReviewBuilder.buildReview(config);

        new LimitCommentVisitor(10).afterReview(review);

        assertThat(review.getFiles()).hasSize(4);
        assertThat(review.getMessages()).containsExactly("Total 8 violations found");
        assertThat(review.getFiles().get(0).getComments()).hasSize(2);
        assertThat(review.getFiles().get(1).getComments()).hasSize(2);
        assertThat(review.getFiles().get(2).getComments()).hasSize(2);
        assertThat(review.getFiles().get(3).getComments()).hasSize(2);
    }

    @Test
    void shouldLimitCommentsIfCountIsHigherMaximumCount() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = ReviewBuilder.buildReview(config);

        new LimitCommentVisitor(3).afterReview(review);

        assertThat(review.getFiles()).hasSize(4);
        assertThat(review.getMessages()).containsExactly("Total 8 violations found",
                "Showing only first 3 comments. Rest 5 comments are filtered out");
        assertThat(review.getFiles().get(0).getComments()).hasSize(2);
        assertThat(review.getFiles().get(1).getComments()).hasSize(1);
        assertThat(review.getFiles().get(2).getComments()).isEmpty();
        assertThat(review.getFiles().get(3).getComments()).isEmpty();
    }
}