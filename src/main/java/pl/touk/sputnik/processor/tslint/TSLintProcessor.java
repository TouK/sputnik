package pl.touk.sputnik.processor.tslint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.processor.tslint.json.ListViolationsResponse;
import pl.touk.sputnik.processor.tslint.json.TSLintFileInfo;
import pl.touk.sputnik.review.*;
import pl.touk.sputnik.review.filter.TypeScriptFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class TSLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "TSLint";

    @NotNull
    private final Configuration config;

    private final TSLintScript tsLintScript;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TSLintProcessor(Configuration config) {
        this.config = config;
        String tsScript = config.getProperty(GeneralOption.TSLINT_SCRIPT);
        String configFile = config.getProperty(GeneralOption.TSLINT_CONFIGURATION_FILE);

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
