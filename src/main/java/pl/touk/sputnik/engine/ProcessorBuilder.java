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
    public static List<ReviewProcessor> buildProcessors(Configuration configuration) {
        List<ReviewProcessor> processors = new ArrayList<>();
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.CHECKSTYLE_ENABLED))) {
            processors.add(new CheckstyleProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.PMD_ENABLED))) {
            processors.add(new PmdProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.FINDBUGS_ENABLED))) {
            processors.add(new FindBugsProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.SCALASTYLE_ENABLED))) {
            processors.add(new ScalastyleProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.CODE_NARC_ENABLED))) {
            processors.add(new CodeNarcProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.JSLINT_ENABLED))) {
            processors.add(new JsLintProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.JSHINT_ENABLED))) {
            processors.add(new JsHintProcessor(configuration));
        }
        if (Boolean.valueOf(configuration.getProperty(GeneralOption.SONAR_ENABLED))) {
            processors.add(new SonarProcessor(configuration));
        }
        return processors;
    }
}
