package pl.touk.sputnik.processor.tslint;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.processor.tslint.json.ListViolationsResponse;
import pl.touk.sputnik.processor.tslint.json.TSLintFileInfo;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.TypeScriptFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class TSLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "TSLint";

    @Setter
    private TSLintScript tsLintScript;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TSLintProcessor() {
        String tsScript = ConfigurationHolder.instance().getProperty(GeneralOption.TSLINT_SCRIPT);
        String configFile = ConfigurationHolder.instance().getProperty(GeneralOption.TSLINT_CONFIGURATION_FILE);

        tsLintScript = new TSLintScript(tsScript, configFile);
        tsLintScript.validateConfiguration();
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
            addToReview(tsLintScript.reviewFile(file.getAbsolutePath()), result);
        }
        return result;
    }

    public void addToReview(String jsonViolations, ReviewResult result) {
        if (StringUtils.isEmpty(jsonViolations)) {
            return;
        }
        try {
            ListViolationsResponse violations = objectMapper
                    .readValue(jsonViolations, ListViolationsResponse.class);
            log.debug(String.format("Converted from json format to %d violations.", violations.size()));
            for (TSLintFileInfo fileInfo : violations) {
                Violation violation = new Violation(fileInfo.getName(), fileInfo.getStartPosition().getLine(),
                        fileInfo.getFailure(), Severity.ERROR);
                result.add(violation);
            }
        } catch (IOException e) {
            throw new GerritException("Error when converting from json format", e);
        }
    }
}
