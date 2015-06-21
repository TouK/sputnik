package pl.touk.sputnik.processor.findbugs;

import edu.umd.cs.findbugs.ClassScreener;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.IClassScreener;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.JavaFilter;
import pl.touk.sputnik.review.locator.BuildDirLocatorFactory;
import pl.touk.sputnik.review.transformer.ClassNameTransformer;

@Slf4j
public class FindBugsProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "FindBugs";
    private final CollectorBugReporter collectorBugReporter;

    public FindBugsProcessor() {
        collectorBugReporter = createBugReporter();
    }

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review, @NotNull Configuration config) {
        FindBugs2 findBugs = createFindBugs2(review, config);
        try {
            findBugs.execute();
        } catch (Exception e) {
            log.error("FindBugs processing error", e);
            throw new ReviewException("FindBugs processing error", e);
        }
        return collectorBugReporter.getReviewResult();
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    public FindBugs2 createFindBugs2(Review review, @NotNull Configuration config) {
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(createProject(review, config));
        findBugs.setBugReporter(collectorBugReporter);
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        findBugs.setClassScreener(createClassScreener(review));
        findBugs.setUserPreferences(createUserPreferences(config));
        findBugs.setNoClassOk(true);
        return findBugs;
    }

    private UserPreferences createUserPreferences(@NotNull Configuration config) {
        UserPreferences userPreferences = UserPreferences.createDefaultUserPreferences();
        String includeFilterFilename = getIncludeFilterFilename(config);
        if (StringUtils.isNotBlank(includeFilterFilename)) {
            userPreferences.getIncludeFilterFiles().put(includeFilterFilename, true);
        }
        String excludeFilterFilename = getExcludeFilterFilename(config);
        if (StringUtils.isNotBlank(excludeFilterFilename)) {
            userPreferences.getExcludeFilterFiles().put(excludeFilterFilename, true);
        }
        return userPreferences;
    }

    @NotNull
    public CollectorBugReporter createBugReporter() {
        CollectorBugReporter collectorBugReporter = new CollectorBugReporter();
        collectorBugReporter.setPriorityThreshold(Priorities.NORMAL_PRIORITY);
        return collectorBugReporter;
    }

    @NotNull
    private Project createProject(@NotNull Review review, @NotNull Configuration config) {
        Project project = new Project();
        for (String buildDir : BuildDirLocatorFactory.create(config).getBuildDirs(review)) {
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
        for (String javaClassName : review.getFiles(new JavaFilter(), new ClassNameTransformer())) {
            classScreener.addAllowedClass(javaClassName);
        }
        return classScreener;
    }

    @Nullable
    private String getIncludeFilterFilename(@NotNull Configuration config) {
        String includeFilterFilename = config.getProperty(GeneralOption.FINDBUGS_INCLUDE_FILTER);
        log.info("Using FindBugs include filter file {}", includeFilterFilename);
        return includeFilterFilename;
    }

    @Nullable
    private String getExcludeFilterFilename(@NotNull Configuration config) {
        String excludeFilterFilename = config.getProperty(GeneralOption.FINDBUGS_EXCLUDE_FILTER);
        log.info("Using FindBugs exclude filter file {}", excludeFilterFilename);
        return excludeFilterFilename;
    }
}
