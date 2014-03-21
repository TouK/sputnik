package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.findbugs.FindBugsProcessor;
import pl.touk.sputnik.gerrit.GerritFacade;
import pl.touk.sputnik.pmd.PmdProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notBlank;

public class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static final String CHECKSTYLE_ENABLED = "checkstyle.enabled";
    private static final String PMD_ENABLED = "pmd.enabled";
    private static final String FINDBUGS_ENABLED = "findbugs.enabled";
    private static final long THOUSAND = 1000L;

    public void run() {
        Configuration.instance().init();

        GerritFacade gerritFacade = createGerritFacade();
        Review review = createReview(gerritFacade);

        List<ReviewProcessor> processors = createProcessors();
        for (ReviewProcessor processor : processors) {
            review(review, processor);
        }

//        gerritFacade.setReview(changeId, revisionId, review.toReviewInput());
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
        return processors;
    }

    @NotNull
    private GerritFacade createGerritFacade() {
        String host = Configuration.instance().getProperty(GerritFacade.GERRIT_HOST);
        String port = Configuration.instance().getProperty(GerritFacade.GERRIT_PORT);
        String username = Configuration.instance().getProperty(GerritFacade.GERRIT_USERNAME);
        String password = Configuration.instance().getProperty(GerritFacade.GERRIT_PASSWORD);

        notBlank(host, "You must provide non blank Gerrit host");
        notBlank(port, "You must provide non blank Gerrit port");
        notBlank(username, "You must provide non blank Gerrit username");
        notBlank(password, "You must provide non blank Gerrit password");

        return new GerritFacade(host, Integer.valueOf(port), username, password);
    }

    @NotNull
    private Review createReview(@NotNull GerritFacade gerritFacade) {
        String changeId = Configuration.instance().getProperty(GerritFacade.GERRIT_CHANGEID);
        String revisionId = Configuration.instance().getProperty(GerritFacade.GERRIT_REVISIONID);
        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        return new Review(gerritFacade.listFiles(changeId, revisionId));

    }
}
