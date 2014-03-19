package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

public class FindBugsProcessor implements ReviewProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FindBugsProcessor.class);
    private CollectorBugReporter collectorBugReporter;

    @Override
    public void process(@NotNull Review review) {
        collectorBugReporter = createBugReporter();
        FindBugs2 findBugs = createFindBugs2(review, collectorBugReporter);
        try {
            findBugs.execute();
        } catch (Throwable e) {
            LOG.error("FindBugs process error", e);
        } finally {
            LOG.info("Process FindBugs finished");
        }
    }

    @Override
    @Nullable
    public ReviewResult getReviewResult() {
        return collectorBugReporter.getReviewResult();
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
    public CollectorBugReporter createBugReporter() {
        CollectorBugReporter collectorBugReporter = new CollectorBugReporter();
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
