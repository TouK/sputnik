package pl.touk.sputnik;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.Severity;

import java.util.Arrays;
import java.util.List;

public class ReviewBuilder {

    public static Review buildReview(Configuration configuration) {
        List<ReviewFile> reviewFiles = Arrays.asList(buildReviewFile(1), buildReviewFile(2), buildReviewFile(3), buildReviewFile(4));
        Review review = new Review(reviewFiles, ReviewFormatterFactory.get(configuration));
        review.getMessages().add("Total 8 violations found");
        review.getScores().put("Code-Review", (short) 1);
        return review;
    }

    private static ReviewFile buildReviewFile(int i) {
        ReviewFile reviewFile = new ReviewFile("filename" + i);
        reviewFile.getComments().add(new Comment(0, "test1", Severity.INFO));
        reviewFile.getComments().add(new Comment(1, "test2", Severity.INFO));
        return reviewFile;
    }
}
