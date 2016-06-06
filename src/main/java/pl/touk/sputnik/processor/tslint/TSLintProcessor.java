package pl.touk.sputnik.processor.tslint;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.filter.TypeScriptFilter;

import java.io.File;

@Slf4j
public class TSLintProcessor extends ProcessorRunningExternalProcess {

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

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    @Override
    public FileFilter getReviewFileFilter() {
        return new TypeScriptFilter();
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return resultParser;
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return tsLintScript.reviewFile(fileToReview.getAbsolutePath());
    }
}
