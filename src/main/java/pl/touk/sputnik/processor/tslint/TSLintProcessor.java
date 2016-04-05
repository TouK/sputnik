package pl.touk.sputnik.processor.tslint;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.TypeScriptFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.util.List;

@Slf4j
public class TSLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "TSLint";

    private final TSLintScript tsLintScript;
    private final TSLintResultParser resultParser;

    public TSLintProcessor(Configuration config) {
        String tsScript = config.getProperty(GeneralOption.TSLINT_SCRIPT);
        String configFile = config.getProperty(GeneralOption.TSLINT_CONFIGURATION_FILE);

        tsLintScript = new TSLintScript(tsScript, configFile);
        tsLintScript.validateConfiguration();
        resultParser = new TSLintResultParser();
    }

    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    @Override
    @NotNull
    public ReviewResult process(Review review) {
        ReviewResult result = new ReviewResult();

        List<File> files = review.getFiles(new TypeScriptFilter(), new IOFileTransformer());
        for (File file : files) {
            for (Violation violation : resultParser.parse(tsLintScript.reviewFile(file.getAbsolutePath()))) {
                result.add(violation);
            }
        }
        return result;
    }

}
