package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Review;
import pl.touk.sputnik.Severity;

public class FindBugsProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FindBugsProcessor.class);
    private static final String SOURCE_NAME = "FindBugs";
    private static final String CHECKSTYLE_CONFIGURATION_FILE = "checkstyle.configurationFile";
    private static final String CHECKSTYLE_PROPERTIES_FILE = "checkstyle.propertiesFile";

    public void process(@NotNull Review review) {
        CollectorBugReporter collectorBugReporter = createBugReporter(review);
        FindBugs2 findBugs = createFindBugs2(review, collectorBugReporter);
        try {
            findBugs.execute();
        } catch (Throwable e) {
            LOG.error("FindBugs process error", e);
        } finally {
            LOG.info("Process FindBugs finished with {} errors", findBugs.getErrorCount() + collectorBugReporter.getBugs().size());
            collectErrors(review, collectorBugReporter);
        }
    }

    public FindBugs2 createFindBugs2(Review review, CollectorBugReporter collectorBugReporter) {
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(createProject(review));
        findBugs.setBugReporter(collectorBugReporter);
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        findBugs.setClassScreener(createClassScreener(review));
        findBugs.setUserPreferences(UserPreferences.createDefaultUserPreferences());
        return findBugs;
    }

    @NotNull
    public CollectorBugReporter createBugReporter(@NotNull Review review) {
        CollectorBugReporter collectorBugReporter = new CollectorBugReporter();
        collectorBugReporter.setPriorityThreshold(Detector.NORMAL_PRIORITY);
        collectorBugReporter.setIoFileToJavaClassNames(review.getIoFileToJavaClassNames());
        return collectorBugReporter;
    }

    @NotNull
    private Project createProject(@NotNull Review review) {
        Project project = new Project();
        for (String buildDir : review.getBuildDirs()) {
            project.addFile(buildDir);
        }
        for (String sourceDir : review.getSourceDirs()) {
            project.addSourceDir(sourceDir);
        }
        return project;
    }

    @NotNull
    private IClassScreener createClassScreener(@NotNull Review review) {
        ClassScreener classScreener = new ClassScreener();
        for (String javaClassName : review.getJavaClassNames()) {
            classScreener.addAllowedClass(javaClassName);
        }
        return classScreener;
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
