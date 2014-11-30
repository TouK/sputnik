package pl.touk.sputnik.engine;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.processor.codenarc.CodeNarcProcessor;
import pl.touk.sputnik.processor.findbugs.FindBugsProcessor;
import pl.touk.sputnik.processor.jshint.JsHintProcessor;
import pl.touk.sputnik.processor.jslint.JsLintProcessor;
import pl.touk.sputnik.processor.pmd.PmdProcessor;
import pl.touk.sputnik.processor.scalastyle.ScalastyleProcessor;
import pl.touk.sputnik.processor.sonar.SonarProcessor;
import pl.touk.sputnik.processor.tslint.TSLintProcessor;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.ArrayList;
import java.util.List;

public class ProcessorBuilder {

    @NotNull
    public List<ReviewProcessor> buildProcessors() {
        List<ReviewProcessor> processors = new ArrayList<>();

        if (isProcessorEnabled(GeneralOption.CHECKSTYLE_ENABLED)) {
            processors.add(new CheckstyleProcessor());
        }
        if (isProcessorEnabled(GeneralOption.PMD_ENABLED)) {
            processors.add(new PmdProcessor());
        }
        if (isProcessorEnabled(GeneralOption.FINDBUGS_ENABLED)) {
            processors.add(new FindBugsProcessor());
        }
        if (isProcessorEnabled(GeneralOption.SCALASTYLE_ENABLED)) {
            processors.add(new ScalastyleProcessor());
        }
        if (isProcessorEnabled(GeneralOption.CODE_NARC_ENABLED)) {
            processors.add(new CodeNarcProcessor());
        }
        if (isProcessorEnabled(GeneralOption.JSLINT_ENABLED)) {
            processors.add(new JsLintProcessor());
        }
        if (isProcessorEnabled(GeneralOption.JSHINT_ENABLED)) {
            processors.add(new JsHintProcessor());
        }
        if (isProcessorEnabled(GeneralOption.TSLINT_ENABLED)) {
            processors.add(new TSLintProcessor());
        }
        if (isProcessorEnabled(GeneralOption.SONAR_ENABLED)) {
            processors.add(new SonarProcessor());
        }
        return processors;
    }

    /**
     * Checks if given processor was enabled in config file or not.
     * 
     * @param processorOption
     *            key informs if processor is enabled
     * @return true if given processor was enabled, otherwise false
     */
    private boolean isProcessorEnabled(GeneralOption processorOption) {
        return Boolean.valueOf(ConfigurationHolder.instance().getProperty(processorOption));
    }
}
