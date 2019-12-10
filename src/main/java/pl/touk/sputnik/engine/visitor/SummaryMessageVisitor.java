package pl.touk.sputnik.engine.visitor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

@Slf4j
@AllArgsConstructor
public class SummaryMessageVisitor implements AfterReviewVisitor {

    /**
     * The message we display when there no problems have been found.
     */
    private final String perfectMessage;

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
        if (review.getTotalViolationCount() == 0L) {
            return perfectMessage;
        }
        String violationNoun = review.getTotalViolationCount() == 1 ? "violation" : "violations";
        return String.format("Total %d %s found", review.getTotalViolationCount(), violationNoun);
    }

    private void addProblemMessages(@NotNull Review review) {
        for (String problemMessage : review.getProblems()) {
            log.info("Adding problem message to review: {}", problemMessage);
            review.getMessages().add(problemMessage);
        }
    }
}
