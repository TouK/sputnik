package pl.touk.sputnik.processor.eslint;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.filter.JavaScriptFilter;

import java.io.File;

@Slf4j
class ESLintProcessor extends ProcessorRunningExternalProcess {

    private final ESLintExecutor executor;

    ESLintProcessor(Configuration configuration) {
        executor = new ESLintExecutor(configuration.getProperty(GeneralOption.ESLINT_RCFILE));
    }

    @Override
    public FileFilter getReviewFileFilter() {
        return new JavaScriptFilter();
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return new ESLintResultParser();
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return executor.runOnFile(fileToReview.getAbsolutePath());
    }

    @NotNull
    @Override
    public String getName() {
        return "ESLint";
    }
}
