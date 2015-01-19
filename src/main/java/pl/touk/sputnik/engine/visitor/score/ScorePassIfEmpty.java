package pl.touk.sputnik.engine.visitor.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.review.Review;

import java.util.Map;

@Slf4j
@Getter
@AllArgsConstructor
public class ScorePassIfEmpty implements AfterReviewVisitor {
    private final Map<String, Short> passingScore;
    private final Map<String, Short> failingScore;

    @Override
    public void afterReview(@NotNull Review review) {
        if (review.getTotalViolationCount() == 0) {
            log.info("Adding passing score {} for no violation(s) found", passingScore);
            review.setScores(passingScore);
            return;
        }

        log.info("Adding failing score {} for {} violations found", failingScore, review.getTotalViolationCount());
        review.setScores(failingScore);
    }
}
