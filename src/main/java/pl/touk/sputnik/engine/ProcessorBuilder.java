package pl.touk.sputnik.engine;

import java.util.ArrayList;
import java.util.List;

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
import pl.touk.sputnik.review.ReviewProcessor;

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
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.JSLINT_ENABLED))) {
            processors.add(new JsLintProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.JSHINT_ENABLED))) {
            processors.add(new JsHintProcessor());
        }
        if (Boolean.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SONAR_ENABLED))) {
            processors.add(new SonarProcessor());
        }
        return processors;
    }
}
