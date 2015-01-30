package pl.touk.sputnik.processor.jslint;

import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.configuration.ConfigurationHolder;
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;

@Slf4j
public class JsLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "JSLint";

    @Override
    public ReviewResult process(Review review) {
        final Properties configProperties = loadBaseProperties();
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
     * Load JSHint configuration property files specified by {@link GeneralOption#JSLINT_CONFIGURATION_FILE}
     * configuration key
     *
     * @return a Properties instance
     */
    private Properties loadBaseProperties() {
        final Properties props = new Properties();
        String configurationFile = ConfigurationHolder.instance().getProperty(GeneralOption.JSLINT_CONFIGURATION_FILE);
        final File propertyFile = new File(StringUtils.strip(configurationFile));
        log.info("Loading {}", propertyFile.getAbsolutePath());
        try {
            props.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new ReviewException("IO exception when reading JSLint configuration file.", e);
        }

        return props;
    }


    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
