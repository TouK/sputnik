package pl.touk.sputnik.review;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.ConnectorFacadeFactory;
import pl.touk.sputnik.processor.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.processor.findbugs.FindBugsProcessor;
import pl.touk.sputnik.processor.pmd.PmdProcessor;
import pl.touk.sputnik.processor.scalastyle.ScalastyleProcessor;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Engine {
    private static final String CHECKSTYLE_ENABLED = "checkstyle.enabled";
    private static final String PMD_ENABLED = "pmd.enabled";
    private static final String FINDBUGS_ENABLED = "findbugs.enabled";
    private static final String SCALASTYLE_ENABLED = "scalastyle.enabled";
    private static final long THOUSAND = 1000L;

    public void run() {
        ConnectorFacade facade = ConnectorFacadeFactory.INSTANCE.build(Configuration.instance().getProperty("cli.connector"));
        List<ReviewFile> reviewFiles = facade.listFiles();
        log.debug("Files to check: " + reviewFiles);

        Review review = new Review(reviewFiles);

        List<ReviewProcessor> processors = createProcessors();
        for (ReviewProcessor processor : processors) {
            review(review, processor);
        }

        facade.setReview(review.toReviewInput());
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

    @NotNull
    private List<ReviewProcessor> createProcessors() {
        List<ReviewProcessor> processors = new ArrayList<ReviewProcessor>();
        if (Boolean.valueOf(Configuration.instance().getProperty(CHECKSTYLE_ENABLED))) {
            processors.add(new CheckstyleProcessor());
        }
        if (Boolean.valueOf(Configuration.instance().getProperty(PMD_ENABLED))) {
            processors.add(new PmdProcessor());
        }
        if (Boolean.valueOf(Configuration.instance().getProperty(FINDBUGS_ENABLED))) {
            processors.add(new FindBugsProcessor());
        }
        if (Boolean.valueOf(Configuration.instance().getProperty(SCALASTYLE_ENABLED))) {
            processors.add(new ScalastyleProcessor());
        }
        return processors;
    }
}
