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
public class ScoreAlwaysPass implements AfterReviewVisitor {
    private final Map<String, Short> passingScore;

    @Override
    public void afterReview(@NotNull Review review) {
        log.info("Adding static passing score {} to review", passingScore);
        review.getScores().putAll(passingScore);
    }
}
