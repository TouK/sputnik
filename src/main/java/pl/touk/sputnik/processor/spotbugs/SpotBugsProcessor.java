package pl.touk.sputnik.processor.spotbugs;

import edu.umd.cs.findbugs.ClassScreener;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.IClassScreener;
import edu.umd.cs.findbugs.Plugin;
import edu.umd.cs.findbugs.PluginException;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
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
public class SpotBugsProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "SpotBugs";

    private final CollectorBugReporter collectorBugReporter;

    private final Configuration config;

    public SpotBugsProcessor(@NotNull Configuration configuration) {
        collectorBugReporter = createBugReporter();
        config = configuration;
    }

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        FindBugs2 spotBugs = createFindBugs2(review);
        try {
            spotBugs.execute();
        } catch (Exception e) {
            log.error("SpotBugs processing error", e);
            throw new ReviewException("SpotBugs processing error", e);
        }
        return collectorBugReporter.getReviewResult();
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    public FindBugs2 createFindBugs2(Review review) {
        loadAllSpotbugsPlugins();
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(createProject(review));
        findBugs.setBugReporter(collectorBugReporter);
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        findBugs.setClassScreener(createClassScreener(review));
        findBugs.setUserPreferences(createUserPreferences());
        findBugs.setNoClassOk(true);
        return findBugs;
    }

    private UserPreferences createUserPreferences() {
        UserPreferences userPreferences = UserPreferences.createDefaultUserPreferences();
        String includeFilterFilename = getIncludeFilterFilename();
        if (StringUtils.isNotBlank(includeFilterFilename)) {
            userPreferences.getIncludeFilterFiles().put(includeFilterFilename, true);
        }
        String excludeFilterFilename = getExcludeFilterFilename();
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
    private Project createProject(@NotNull Review review) {
        Project project = new Project();
        for (String buildDir : BuildDirLocatorFactory.create(config).getBuildDirs(review)) {
            project.addFile(buildDir);
        }
        project.addSourceDirs(review.getSourceDirs());
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
    private String getIncludeFilterFilename() {
        String includeFilterFilename = config.getProperty(GeneralOption.SPOTBUGS_INCLUDE_FILTER);
        if (StringUtils.isBlank(includeFilterFilename)) {
            includeFilterFilename = config.getProperty(GeneralOption.FINDBUGS_INCLUDE_FILTER);
        }
        log.info("Using SpotBugs include filter file {}", includeFilterFilename);
        return includeFilterFilename;
    }

    @Nullable
    private String getExcludeFilterFilename() {
        String excludeFilterFilename = config.getProperty(GeneralOption.SPOTBUGS_EXCLUDE_FILTER);
        if (StringUtils.isBlank(excludeFilterFilename)) {
            excludeFilterFilename = config.getProperty(GeneralOption.FINDBUGS_EXCLUDE_FILTER);
        }
        log.info("Using SpotBugs exclude filter file {}", excludeFilterFilename);
        return excludeFilterFilename;
    }

    private void loadAllSpotbugsPlugins() {
        String pluginsLocation = config.getProperty(GeneralOption.SPOTBUGS_PLUGINS_LOCATION);
        if (pluginsLocation != null) {
            File[] pluginsList = new File(pluginsLocation).listFiles();
            for (File plugin : Objects.requireNonNull(pluginsList)) {
                if (plugin.getName().contains(".jar")) {
                    log.info("SpotBugs additional plugin loading: file://{}", plugin);
                    try {
                        Plugin.getAllPlugins().add(Plugin.addCustomPlugin(new URI("file://" + plugin.getAbsoluteFile())));
                    } catch (PluginException e) {
                        log.info("Spotbugs additional plugins not loaded {} plugin not supported", e.getMessage());
                    } catch (URISyntaxException e) {
                        log.info("Spotbugs additional plugins not loaded {} check path", e.getMessage());
                    }
                }
            }
        }
    }
}
