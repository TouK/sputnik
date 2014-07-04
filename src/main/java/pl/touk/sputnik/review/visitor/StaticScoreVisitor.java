package pl.touk.sputnik.review.visitor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class StaticScoreVisitor implements AfterReviewVisitor {
    private Map<String, Integer> scores;

    @Override
    public void afterReview(@NotNull Review review) {
        log.info("Adding static score {} to review", scores);
        review.getScores().putAll(scores);
    }
}
