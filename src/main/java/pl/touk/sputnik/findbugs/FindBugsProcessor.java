package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.Severity;

public class FindBugsProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FindBugsProcessor.class);

    public void process(@NotNull Review review) {
        CollectorBugReporter collectorBugReporter = createBugReporter(review);
        FindBugs2 findBugs = createFindBugs2(review, collectorBugReporter);
        try {
            findBugs.execute();
        } catch (Throwable e) {
            LOG.error("FindBugs process error", e);
        } finally {
            LOG.info("Process FindBugs finished");
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
        CollectorBugReporter collectorBugReporter = new CollectorBugReporter(review);
        collectorBugReporter.setPriorityThreshold(Detector.NORMAL_PRIORITY);
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
}
