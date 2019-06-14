package pl.touk.sputnik.engine;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

@Slf4j
@AllArgsConstructor
public class ReviewRunner {
    private static final long THOUSAND = 1000L;

    @NotNull private final Review review;

    public void review(@NotNull ReviewProcessor processor) {
        log.info("Review started for processor {}", processor.getName());
        long start = System.currentTimeMillis();
        ReviewResult reviewResult = null;

        try {
            reviewResult = processor.process(review);
        } catch (ReviewException e) {
            log.error("Processor {} error", processor.getName(), e);
            review.addProblem(processor.getName(), ExceptionUtils.getRootCauseMessage(e));

        }
        log.info("Review finished for processor {}. Took {} s", processor.getName(), (System.currentTimeMillis() - start) / THOUSAND);

        if (reviewResult == null) {
            log.warn("Review for processor {} returned empty review", processor.getName());
        } else {
            log.info("Review for processor {} returned {} violations", processor.getName(), reviewResult.getViolations().size());
            review.add(processor.getName(), reviewResult);
        }
    }
}
