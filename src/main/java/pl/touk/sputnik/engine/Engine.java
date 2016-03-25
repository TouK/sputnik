package pl.touk.sputnik.engine;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

import java.util.List;

@Slf4j
public class Engine {
    private static final long THOUSAND = 1000L;
    private final ConnectorFacade facade;

    public Engine(ConnectorFacade facade) {
        this.facade = facade;
    }

    public void run() {
        final List<ReviewFile> reviewFiles = facade.listFiles();
        final Review toReview = new Review(reviewFiles);

        new VisitorBuilder().buildBeforeReviewVisitors()
                .forEach(brv -> brv.beforeReview(toReview));

        new ProcessorBuilder().buildProcessors()
                .forEach(p -> review(toReview, p));

        new VisitorBuilder().buildAfterReviewVisitors()
                .forEach(arv -> arv.afterReview(toReview));

        facade.setReview(toReview);
    }

    private void review(@NotNull Review review, @NotNull ReviewProcessor processor) {
        log.info("Review started for processor {}", processor.getName());
        long start = System.currentTimeMillis();

        ReviewResult reviewResult = processor.process(review);
        log.info("Review finished for processor {}. Took {} s", processor.getName(), (System.currentTimeMillis() - start) / THOUSAND);

        if (reviewResult == null) {
            log.warn("Review for processor {} returned empty review", processor.getName());
        } else {
            log.info("Review for processor {} returned {} violations", processor.getName(), reviewResult.getViolations().size());
            review.add(processor.getName(), reviewResult);
        }
    }


}
