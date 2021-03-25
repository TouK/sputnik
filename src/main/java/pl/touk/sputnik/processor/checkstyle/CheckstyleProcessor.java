package pl.touk.sputnik.processor.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import lombok.AllArgsConstructor;
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
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
public class CheckstyleProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "Checkstyle";
    private final CollectorListener collectorListener = new CollectorListener();
    private final Configuration configuration;

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        innerProcess(review, collectorListener);
        return collectorListener.getReviewResult();
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    private void innerProcess(@NotNull Review review, @NotNull AuditListener auditListener) {
        List<File> files = review.getFiles(new JavaFilter(), new IOFileTransformer());
        Checker checker = createChecker(auditListener);
        try {
            checker.process(files);
        } catch (CheckstyleException e) {
            throw new ReviewException("Unable to process files with Checkstyle", e);
        }
        checker.destroy();
    }

    @NotNull
    private Checker createChecker(@NotNull AuditListener auditListener) {
        try {
            String configurationFile = getConfigurationFilename();
            if (StringUtils.isBlank(configurationFile)) {
                throw new ReviewException("Checkstyle configuration file is not specified.");
            }
            if (!Files.exists(Paths.get(configurationFile))) {
                throw new ReviewException("Checkstyle configuration file does not exist.");
            }

            Checker checker = new Checker();
            ClassLoader moduleClassLoader = Checker.class.getClassLoader();

            Properties properties = new Properties(System.getProperties());
            properties.setProperty("config_loc", new File(configurationFile).getParent());

            checker.setModuleClassLoader(moduleClassLoader);
            checker.configure(ConfigurationLoader.loadConfiguration(configurationFile, new PropertiesExpander(properties)));
            checker.addListener(auditListener);
            return checker;
        } catch (CheckstyleException e) {
            throw new ReviewException("Unable to create Checkstyle checker", e);
        }
    }

    @Nullable
    private String getConfigurationFilename() {
        String configurationFile = configuration.getProperty(GeneralOption.CHECKSTYLE_CONFIGURATION_FILE);
        log.info("Using Checkstyle configuration file {}", configurationFile);
        return configurationFile;
    }
}
