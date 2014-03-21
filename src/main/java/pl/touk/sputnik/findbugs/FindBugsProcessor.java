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
    private static final String SOURCE_NAME = "FindBugs";
    private CollectorBugReporter collectorBugReporter;

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        createBugReporter();
        FindBugs2 findBugs = createFindBugs2(review);
        try {
            findBugs.execute();
        } catch (Throwable e) {
            LOG.error("FindBugs process error", e);
        }
        return collectorBugReporter.getReviewResult();
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    public FindBugs2 createFindBugs2(Review review) {
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
        collectorBugReporter = new CollectorBugReporter();
        collectorBugReporter.setPriorityThreshold(Priorities.NORMAL_PRIORITY);
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
