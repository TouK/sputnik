package pl.touk.sputnik.engine.visitor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

@Slf4j
public class SummaryMessageVisitor implements AfterReviewVisitor {

    @Override
    public void afterReview(@NotNull Review review) {
        String message = String.format("Total %d violations found", review.getTotalViolationCount());
        log.info("Adding summary message to review: {}", message);
        review.getMessages().add(message);
    }
}
