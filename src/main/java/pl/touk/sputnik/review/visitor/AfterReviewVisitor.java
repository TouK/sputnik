package pl.touk.sputnik.review.visitor;

import pl.touk.sputnik.review.Review;

public interface AfterReviewVisitor {

    void afterReview(Review review);
}
