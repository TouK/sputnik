package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.ConnectorFacade;
import pl.touk.sputnik.ConnectorFacadeFactory;
import pl.touk.sputnik.Patchset;
import pl.touk.sputnik.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.findbugs.FindBugsProcessor;
import pl.touk.sputnik.gerrit.GerritFacade;
import pl.touk.sputnik.gerrit.GerritPatchset;
import pl.touk.sputnik.pmd.PmdProcessor;
import pl.touk.sputnik.scalastyle.ScalastyleProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notBlank;

public class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static final String CHECKSTYLE_ENABLED = "checkstyle.enabled";
    private static final String PMD_ENABLED = "pmd.enabled";
    private static final String FINDBUGS_ENABLED = "findbugs.enabled";
    private static final String SCALASTYLE_ENABLED = "scalastyle.enabled";
    private static final long THOUSAND = 1000L;

    public void run() {
        ConnectorFacade facade = ConnectorFacadeFactory.get(Configuration.instance().getConnectorName());
        Patchset patchSet = facade.createPatchset();
        List<ReviewFile> reviewFiles = facade.listFiles(patchSet);
        //List<ReviewFile> reviewFiles = gerritFacade.listFiles(gerritPatchset);
        /*List<ReviewFile> reviewFiles = ImmutableList.of(
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/web/Web.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/core/core.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/core/registration.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/core/User.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/api/ClientService.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/api/Api.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/api/StaticPagesService.scala"),
                new ReviewFile("./src/main/scala/pl/ftang/scala/polka/api/services.scala"));*/
        Review review = new Review(reviewFiles);

        List<ReviewProcessor> processors = createProcessors();
        for (ReviewProcessor processor : processors) {
            review(review, processor);
        }

        facade.setReview(patchSet, review.toReviewInput());
        //gerritFacade.setReview(gerritPatchset, review.toReviewInput());
    }

    private void review(@NotNull Review review, @NotNull ReviewProcessor processor) {
        LOG.info("Review started for processor {}", processor.getName());
        long start = System.currentTimeMillis();

        ReviewResult reviewResult = processor.process(review);
        LOG.info("Review finished for processor {}. Took {} s", processor.getName(), (System.currentTimeMillis() - start) / THOUSAND);

        if (reviewResult == null) {
            LOG.warn("Review for processor {} returned empty review", processor.getName());
        } else {
            LOG.info("Review for processor {} returned {} violations", processor.getName(), reviewResult.getViolations().size());
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
