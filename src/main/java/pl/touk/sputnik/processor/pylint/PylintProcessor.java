package pl.touk.sputnik.processor.pylint;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.filter.PythonFilter;

import java.io.File;

@Slf4j
class PylintProcessor extends ProcessorRunningExternalProcess {

    private PylintExecutor pylintExecutor;
    private PylintResultParser pylintResultParser;

    PylintProcessor(Configuration configuration) {
        pylintExecutor = new PylintExecutor(configuration.getProperty(GeneralOption.PYLINT_RCFILE));
        pylintResultParser = new PylintResultParser();
    }

    @NotNull
    @Override
    public String getName() {
        return "Pylint";
    }

    @Override
    public FileFilter getReviewFileFilter() {
        return new PythonFilter();
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return pylintResultParser;
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return pylintExecutor.runOnFile(fileToReview.getAbsolutePath());
    }
}
