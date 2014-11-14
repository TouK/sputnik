package pl.touk.sputnik.engine.visitor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

@Slf4j
public class SummaryMessageVisitor implements AfterReviewVisitor {
    private static final String PERFECT_MESSAGE = "Perfect!";

    @Override
    public void afterReview(@NotNull Review review) {
        addSummaryMessage(review);
        addProblemMessages(review);
    }

    private void addSummaryMessage(Review review) {
        String summaryMessage = getSummaryMessage(review);
        log.info("Adding summary message to review: {}", summaryMessage);
        review.getMessages().add(summaryMessage);
    }

    private String getSummaryMessage(@NotNull Review review) {
        if (review.getTotalViolationCount() == 0) {
            return PERFECT_MESSAGE;
        }
        return String.format("Total %d violations found", review.getTotalViolationCount());
    }

    private void addProblemMessages(@NotNull Review review) {
        for (String problemMessage : review.getProblems()) {
            log.info("Adding problem message to review: {}", problemMessage);
            review.getMessages().add(problemMessage);
        }
    }
}
