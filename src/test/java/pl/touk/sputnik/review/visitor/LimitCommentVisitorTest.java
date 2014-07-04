package pl.touk.sputnik.review.visitor;

import org.junit.Test;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitCommentVisitorTest {

    @Test
    public void shouldNotLimitCommentsIfCountIsBelowMaximumCount() {
        Review review = buildReview();

        new LimitCommentVisitor(10).afterReview(review);

        assertThat(review.getFiles()).hasSize(4);
        assertThat(review.getMessages()).isEmpty();
        assertThat(review.getFiles().get(0).getComments()).hasSize(2);
        assertThat(review.getFiles().get(1).getComments()).hasSize(2);
        assertThat(review.getFiles().get(2).getComments()).hasSize(2);
        assertThat(review.getFiles().get(3).getComments()).hasSize(2);
    }

    @Test
    public void shouldLimitCommentsIfCountIsHigherMaximumCount() {
        Review review = buildReview();

        new LimitCommentVisitor(3).afterReview(review);

        assertThat(review.getFiles()).hasSize(4);
        assertThat(review.getMessages()).hasSize(1);
        assertThat(review.getMessages().get(0)).isEqualTo("Showing only first 3 comments. 5 comments are filtered out.");
        assertThat(review.getFiles().get(0).getComments()).hasSize(2);
        assertThat(review.getFiles().get(1).getComments()).hasSize(1);
        assertThat(review.getFiles().get(2).getComments()).isEmpty();
        assertThat(review.getFiles().get(3).getComments()).isEmpty();
    }

    private Review buildReview() {
        List<ReviewFile> reviewFiles = Arrays.asList(buildReviewFile(), buildReviewFile(), buildReviewFile(), buildReviewFile());
        Review review = new Review(reviewFiles);
        review.setTotalViolationsCount(8);
        return review;
    }

    private ReviewFile buildReviewFile() {
        ReviewFile reviewFile = new ReviewFile("test");
        reviewFile.getComments().add(new Comment(0, "test"));
        reviewFile.getComments().add(new Comment(0, "test"));
        return reviewFile;
    }

}