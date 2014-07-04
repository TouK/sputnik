package pl.touk.sputnik.engine.visitor;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

public interface AfterReviewVisitor {

    void afterReview(@NotNull Review review);
}
