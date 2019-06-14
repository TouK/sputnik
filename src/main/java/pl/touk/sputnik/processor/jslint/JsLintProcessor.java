package pl.touk.sputnik.processor.jslint;

import com.google.common.base.Strings;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.JavaScriptFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
public class JsLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "JSLint";
    @NotNull
    private final Configuration config;

    @Override
    public ReviewResult process(Review review) {
        final Properties configProperties = loadConfigurationProperties();
        return toReviewResult(lint(review, configProperties));
    }

    private ReviewResult toReviewResult(List<Issue> lintIssues) {
        ReviewResult result = new ReviewResult();
        for (Issue issue : lintIssues) {
            result.add(new Violation(issue.getSystemId(), issue.getLine(), issue.getReason(), Severity.INFO));
        }
        return result;
    }

    private List<Issue> lint(Review review, Properties configProperties) {
        JSLint jsLint = new JSLintBuilder().fromDefault();
        applyProperties(jsLint, configProperties);
        List<Issue> lintIssues = new ArrayList<>();
        List<File> files = review.getFiles(new JavaScriptFilter(), new IOFileTransformer());
        for (File file : files) {
            lintIssues.addAll(lintFile(jsLint, file));
        }
        return lintIssues;
    }

    private void applyProperties(JSLint jsLint, Properties configProperties) {
        if (configProperties == null) {
            return;
        }
        for (String propertyName : configProperties.stringPropertyNames()) {
            Option option;
            try {
                option = Option.valueOf(propertyName.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown JSLint configuration property '{}'. Continue.", propertyName);
                continue;
            }
            jsLint.addOption(option, configProperties.getProperty(propertyName));
        }
    }

    private List<Issue> lintFile(JSLint jsLint, File file) {
        try (FileReader fileReader = new FileReader(file)) {
            JSLintResult lintResult = jsLint.lint(file.getAbsolutePath(), fileReader);
            return lintResult.getIssues();
        } catch (IOException e) {
            throw new ReviewException("IO exception when running JSLint.", e);
        }
    }

    /**
     * Load JSLint configuration property file specified by {@link GeneralOption#JSLINT_CONFIGURATION_FILE}
     * configuration key
     *
     * @return a Properties instance
     */
    private Properties loadConfigurationProperties() {
        String configurationFileName = getConfigurationFileName();
        if (Strings.isNullOrEmpty(configurationFileName)) {
            log.info("JSLint property file not specified. Using default configuration.");
            return null;
        }
        return loadProperties(configurationFileName);
    }

    private Properties loadProperties(String configurationFileName) {
        final Properties props = new Properties();
        try (FileReader fileReader = new FileReader(configurationFileName)) {
            log.info("Loading {}", configurationFileName);
            props.load(fileReader);
        } catch (IOException e) {
            log.error("Load JSLint properties operation failed", e);
            throw new ReviewException("IO exception when reading JSLint configuration file.", e);
        }
        return props;
    }

    private String getConfigurationFileName() {
        String configurationFile = config.getProperty(GeneralOption.JSLINT_CONFIGURATION_FILE);
        log.info("Using JSLint configuration file {}", configurationFile);
        return configurationFile;
    }


    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
