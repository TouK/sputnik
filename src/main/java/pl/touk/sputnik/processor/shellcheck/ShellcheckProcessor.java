package pl.touk.sputnik.processor.shellcheck;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.filter.ShellFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
class ShellcheckProcessor extends ProcessorRunningExternalProcess {

    private ShellcheckExecutor shellcheckExecutor;
    private ShellcheckResultParser shellcheckResultParser;

    ShellcheckProcessor(Configuration configuration) {
        shellcheckExecutor = new ShellcheckExecutor(getExcludeRules(configuration));
        shellcheckResultParser = new ShellcheckResultParser();
    }

    @NotNull
    @Override
    public String getName() {
        return "Shellcheck";
    }

    @Override
    public FileFilter getReviewFileFilter() {
        return new ShellFilter();
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return shellcheckResultParser;
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return shellcheckExecutor.runOnFile(fileToReview.getAbsolutePath());
    }

    @NotNull
    private List<String> getExcludeRules(Configuration configuration) {
        String excludeRules = configuration.getProperty(GeneralOption.SHELLCHECK_EXCLUDE);
        if (excludeRules == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(excludeRules.split(","));
    }
}
