package pl.touk.sputnik.engine;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
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

import java.util.ArrayList;
import java.util.List;

public class ProcessorBuilder {

    @NotNull
    public List<ReviewProcessor> buildProcessors(Configuration configuration) {
        List<ReviewProcessor> processors = new ArrayList<>();
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.CHECKSTYLE_ENABLED))) {
            processors.add(new CheckstyleProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.PMD_ENABLED))) {
            processors.add(new PmdProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.FINDBUGS_ENABLED))) {
            processors.add(new FindBugsProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.SCALASTYLE_ENABLED))) {
            processors.add(new ScalastyleProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.CODE_NARC_ENABLED))) {
            processors.add(new CodeNarcProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.JSLINT_ENABLED))) {
            processors.add(new JsLintProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.JSHINT_ENABLED))) {
            processors.add(new JsHintProcessor());
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.SONAR_ENABLED))) {
            processors.add(new SonarProcessor());
        }
        return processors;
    }
}
