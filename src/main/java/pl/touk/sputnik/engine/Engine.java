package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.connector.ConnectorFacade;

import java.util.ArrayList;
import java.util.List;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.engine.visitor.*;

@Slf4j
public class Engine {
    private static final long THOUSAND = 1000L;
    private final ConnectorFacade facade;

    public Engine(ConnectorFacade facade) {
        this.facade = facade;
    }

    public void run() {
        List<ReviewFile> reviewFiles = facade.listFiles();
        Review review = new Review(reviewFiles);

        for (BeforeReviewVisitor beforeReviewVisitor : new VisitorBuilder().buildBeforeReviewVisitors()) {
            beforeReviewVisitor.beforeReview(review);
        }

        List<ReviewProcessor> processors = new ProcessorBuilder().buildProcessors();
        for (ReviewProcessor processor : processors) {
            review(review, processor);
        }

        for (AfterReviewVisitor afterReviewVisitor : new VisitorBuilder().buildAfterReviewVisitors()) {
            afterReviewVisitor.afterReview(review);
        }

        facade.setReview(review);
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
