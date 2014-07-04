package pl.touk.sputnik.review.visitor;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

public interface BeforeReviewVisitor {

    void beforeReview(@NotNull Review review);
}
