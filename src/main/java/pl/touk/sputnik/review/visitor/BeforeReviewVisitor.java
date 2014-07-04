package pl.touk.sputnik.review.visitor;

import pl.touk.sputnik.review.Review;

public interface BeforeReviewVisitor {

    void beforeReview(Review review);
}
