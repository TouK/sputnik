package pl.touk.sputnik.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.Review;
import pl.touk.sputnik.ReviewException;
import pl.touk.sputnik.Severity;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class CheckstyleProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(CheckstyleProcessor.class);
    private static final String SOURCE_NAME = "Checkstyle";
    private static final String CHECKSTYLE_CONFIGURATION_FILE = "checkstyle.configurationFile";
    private static final String CHECKSTYLE_PROPERTIES_FILE = "checkstyle.propertiesFile";

    public void process(@NotNull Review review) {
        CollectorListener collectorListener = new CollectorListener();
        innerProcess(review, collectorListener);
        collectErrors(review, collectorListener);
    }

    private void innerProcess(@NotNull Review review, @NotNull AuditListener auditListener) {
        LOG.info("Process Checktyle started");
        List<File> files = review.getIOFiles();
        Checker checker = createChecker(auditListener);
        int numErrs = checker.process(files);
        checker.destroy();
        LOG.info("Process Checktyle finished with {} errors", numErrs);
    }

    private void collectErrors(Review review, CollectorListener collectorListener) {
        for (AuditEvent auditEvent : collectorListener.getErrors()) {
            review.addError(auditEvent.getFileName(), SOURCE_NAME, auditEvent.getLine(), auditEvent.getMessage(), convert(auditEvent.getSeverityLevel()));
        }
    }

    @NotNull
    private Checker createChecker(@NotNull AuditListener auditListener) {
        try {
            Checker checker = new Checker();
            ClassLoader moduleClassLoader = Checker.class.getClassLoader();
            String configurationFile = getConfigurationFile();
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
    private String getConfigurationFile() {
        String configurationFile = Configuration.instance().getProperty(CHECKSTYLE_CONFIGURATION_FILE);
        LOG.info("Using Checkstyle configuration file {}", configurationFile);
        return configurationFile;
    }

    @NotNull
    private Severity convert(SeverityLevel severityLevel) {
        switch (severityLevel) {
            case IGNORE:
                return Severity.IGNORE;
            case INFO:
                return Severity.INFO;
            case WARNING:
                return Severity.WARNING;
            case ERROR:
                return Severity.ERROR;
            default:
                throw new IllegalArgumentException("Severity " + severityLevel + " is not supported");
        }
    }

}
