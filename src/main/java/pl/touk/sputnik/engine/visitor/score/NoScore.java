package pl.touk.sputnik.engine.visitor.score;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.review.Review;

@Slf4j
public class NoScore implements AfterReviewVisitor {

    @Override
    public void afterReview(@NotNull Review review) {
        log.info("No score for review");
    }
}
