package pl.touk.sputnik.engine;

import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.BeforeReviewVisitor;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.List;

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
        ReviewRunner reviewRunner = new ReviewRunner(review);
        for (ReviewProcessor processor : processors) {
            reviewRunner.review(processor);
        }

        for (AfterReviewVisitor afterReviewVisitor : new VisitorBuilder().buildAfterReviewVisitors()) {
            afterReviewVisitor.afterReview(review);
        }

        facade.setReview(review);
    }
}
