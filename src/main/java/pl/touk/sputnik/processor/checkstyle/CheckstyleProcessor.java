package pl.touk.sputnik.processor.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.review.*;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class CheckstyleProcessor implements ReviewProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(CheckstyleProcessor.class);
    private static final String SOURCE_NAME = "Checkstyle";
    private static final String CHECKSTYLE_CONFIGURATION_FILE = "checkstyle.configurationFile";
    private static final String CHECKSTYLE_PROPERTIES_FILE = "checkstyle.propertiesFile";
    private final CollectorListener collectorListener = new CollectorListener();

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
        List<File> files = review.getIOFiles();
        Checker checker = createChecker(auditListener);
        checker.process(files);
        checker.destroy();
    }

    @NotNull
    private Checker createChecker(@NotNull AuditListener auditListener) {
        try {
            Checker checker = new Checker();
            ClassLoader moduleClassLoader = Checker.class.getClassLoader();
            String configurationFile = getConfigurationFilename();
            Properties properties = System.getProperties();// loadProperties(new File(System.getProperty(CHECKSTYLE_PROPERTIES_FILE)));
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
        String configurationFile = Configuration.instance().getProperty(CHECKSTYLE_CONFIGURATION_FILE);
        LOG.info("Using Checkstyle configuration file {}", configurationFile);
        return configurationFile;
    }



}
