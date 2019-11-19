package pl.touk.sputnik.engine;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.ReviewPublisher;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.BeforeReviewVisitor;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class Engine {

    private final ConnectorFacade facade;
    private final ReviewPublisher reviewPublisher;
    private final Configuration config;

    public void run() {
        List<ReviewFile> reviewFiles = facade.listFiles();
        Review review = new Review(reviewFiles, ReviewFormatterFactory.get(config));

        for (BeforeReviewVisitor beforeReviewVisitor : new VisitorBuilder().buildBeforeReviewVisitors(config)) {
            beforeReviewVisitor.beforeReview(review);
        }

        List<ReviewProcessor> processors = ProcessorBuilder.buildProcessors(config);
        ReviewRunner reviewRunner = new ReviewRunner(review);
        for (ReviewProcessor processor : processors) {
            reviewRunner.review(processor);
        }

        for (AfterReviewVisitor afterReviewVisitor : new VisitorBuilder().buildAfterReviewVisitors(config, facade)) {
            afterReviewVisitor.afterReview(review);
        }

        reviewPublisher.publish(review);
    }
}
