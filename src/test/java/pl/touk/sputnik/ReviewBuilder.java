package pl.touk.sputnik;

import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.Arrays;
import java.util.List;

public class ReviewBuilder {

    public static Review buildReview() {
        List<ReviewFile> reviewFiles = Arrays.asList(buildReviewFile(1), buildReviewFile(2), buildReviewFile(3), buildReviewFile(4));
        Review review = new Review(reviewFiles);
        review.setTotalViolationsCount(8);
        review.getMessages().add("Total 8 violations found");
        review.getScores().put("Code-Review", 1);
        return review;
    }

    public static ReviewFile buildReviewFile(int i) {
        ReviewFile reviewFile = new ReviewFile("filename" + i);
        reviewFile.getComments().add(new Comment(0, "test1"));
        reviewFile.getComments().add(new Comment(1, "test2"));
        return reviewFile;
    }
}
