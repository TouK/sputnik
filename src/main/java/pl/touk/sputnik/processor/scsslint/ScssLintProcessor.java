package pl.touk.sputnik.processor.scsslint;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.processor.scsslint.json.ScssChangeDetails;
import pl.touk.sputnik.processor.scsslint.json.ScssLintListViolationsResponse;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.ScssFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class ScssLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "scss-lint";

    @Setter
    private ScssLintScript tsLintScript;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScssLintProcessor() {
        String configFile = ConfigurationHolder.instance().getProperty(GeneralOption.SCSSLINT_CONFIGURATION_FILE);

        tsLintScript = new ScssLintScript(configFile);
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

        List<File> files = review.getFiles(new ScssFilter(), new IOFileTransformer());
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
            ScssLintListViolationsResponse violations = objectMapper.readValue(jsonViolations,
                    ScssLintListViolationsResponse.class);
            log.debug(String.format("Converted from json format to %d violations.", violations.size()));
            for (String fileName : violations.keySet()) {
                for (ScssChangeDetails violationDetail : violations.get(fileName)) {
                    Violation violation = buildViolation(fileName, violationDetail);
                    result.add(violation);
                }
            }
        } catch (IOException e) {
            throw new GerritException("Error when converting from json format", e);
        }
    }

    private Violation buildViolation(String fileName, ScssChangeDetails scssChange) {
        return new Violation(fileName, scssChange.getLine(),
                String.format("[%s] %s", scssChange.getLinter(),
                scssChange.getReason()), Severity.ERROR);
    }
}