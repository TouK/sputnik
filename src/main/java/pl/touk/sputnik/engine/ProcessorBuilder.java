package pl.touk.sputnik.engine;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.processor.codenarc.CodeNarcProcessor;
import pl.touk.sputnik.processor.findbugs.FindBugsProcessor;
import pl.touk.sputnik.processor.jslint.JSLintProcessor;
import pl.touk.sputnik.processor.pmd.PmdProcessor;
import pl.touk.sputnik.processor.scalastyle.ScalastyleProcessor;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.ArrayList;
import java.util.List;

public class ProcessorBuilder {

    @NotNull
    public List<ReviewProcessor> buildProcessors() {
        List<ReviewProcessor> processors = new ArrayList<>();
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.CHECKSTYLE_ENABLED))) {
            processors.add(new CheckstyleProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.PMD_ENABLED))) {
            processors.add(new PmdProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.FINDBUGS_ENABLED))) {
            processors.add(new FindBugsProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SCALASTYLE_ENABLED))) {
            processors.add(new ScalastyleProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.CODE_NARC_ENABLED))) {
            processors.add(new CodeNarcProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.JS_LINT_ENABLED))) {
            processors.add(new JSLintProcessor());
        }
        return processors;
    }
}
