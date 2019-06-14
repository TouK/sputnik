package pl.touk.sputnik.processor.jshint;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.jshint4j.Error;
import pl.touk.jshint4j.JsHint;
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
import java.util.List;

@Slf4j
@AllArgsConstructor
public class JsHintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "JSHint";

    @NotNull
    private final Configuration config;

    @Override
    public ReviewResult process(Review review) {
        String configuration = readConfiguration();
        ReviewResult result = new ReviewResult();
        List<File> files = review.getFiles(new JavaScriptFilter(), new IOFileTransformer());
        for (File file : files) {
            processFile(result, file, configuration);
        }
        return result;
    }

    private void processFile(ReviewResult result, File file, String configuration) {
        for (Error issue : lintFile(file, configuration)) {
            result.add(new Violation(file.getAbsolutePath(), issue.getLine(), issue.getReason(), Severity.INFO));
        }
    }

    private List<Error> lintFile(File file, String configuration) {
        JsHint jsHint = new JsHint();
        try (FileReader fileReader = new FileReader(file)) {
            return jsHint.lint(CharStreams.toString(fileReader), configuration);
        } catch (IOException e) {
            throw new ReviewException("IO exception when running JSHint.", e);
        }
    }

    private String readConfiguration() {
        String configurationFileName = getConfigurationFileName();
        if (Strings.isNullOrEmpty(configurationFileName)) {
            return null;
        }
        try (FileReader configurationFileReader = new FileReader(configurationFileName)) {
            return CharStreams.toString(configurationFileReader);
        } catch (IOException e) {
            throw new ReviewException("IO exception when reading JSHint configuration.", e);
        }
    }

    private String getConfigurationFileName() {
        String configurationFile = config.getProperty(GeneralOption.JSHINT_CONFIGURATION_FILE);
        log.info("Using JSHint configuration file {}", configurationFile);
        return configurationFile;
    }

    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
