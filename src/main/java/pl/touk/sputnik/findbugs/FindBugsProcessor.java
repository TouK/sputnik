package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Priorities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.Review;
import pl.touk.sputnik.Severity;

public class FindBugsProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FindBugsProcessor.class);
    private static final String SOURCE_NAME = "FindBugs";
    private static final String CHECKSTYLE_CONFIGURATION_FILE = "checkstyle.configurationFile";
    private static final String CHECKSTYLE_PROPERTIES_FILE = "checkstyle.propertiesFile";

    public void process(@NotNull Review review) {
        CollectorBugReporter collectorBugReporter = new CollectorBugReporter();
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setBugReporter(collectorBugReporter);
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        try {
            findBugs.execute();
        } catch (Throwable e) {
            LOG.error("FindBugs process error", e);
        } finally {
            LOG.info("Process FindBugs finished with {} errors", findBugs.getErrorCount());
            collectErrors(review, collectorBugReporter);
        }
    }

    private void collectErrors(Review review, CollectorBugReporter collectorBugReporter) {
        for (BugInstance bugInstance : collectorBugReporter.getBugs()) {
            review.addError(
                bugInstance.getType(),
                SOURCE_NAME,
                bugInstance.getPrimarySourceLineAnnotation().getStartLine(),
                bugInstance.getMessage(),
                convert(bugInstance.getPriority()));
        }
    }

    @Nullable
    private String getConfigurationFile() {
        String configurationFile = Configuration.instance().getProperty(CHECKSTYLE_CONFIGURATION_FILE);
        LOG.info("Using Checkstyle configuration file {}", configurationFile);
        return configurationFile;
    }

    @NotNull
    private Severity convert(int priority) {
        switch (priority) {
            case Priorities.IGNORE_PRIORITY:
                return Severity.IGNORE;
            case Priorities.EXP_PRIORITY:
            case Priorities.LOW_PRIORITY:
            case Priorities.NORMAL_PRIORITY:
                return Severity.INFO;
            case Priorities.HIGH_PRIORITY:
                return Severity.WARNING;
            default:
                throw new IllegalArgumentException("Priority " + priority + " is not supported");
        }
    }
}
