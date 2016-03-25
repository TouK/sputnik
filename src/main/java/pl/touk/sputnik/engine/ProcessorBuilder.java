package pl.touk.sputnik.engine;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.checkstyle.CheckstyleProcessor;
import pl.touk.sputnik.processor.codenarc.CodeNarcProcessor;
import pl.touk.sputnik.processor.findbugs.FindBugsProcessor;
import pl.touk.sputnik.processor.pmd.PmdProcessor;
import pl.touk.sputnik.processor.scalastyle.ScalastyleProcessor;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.ArrayList;
import java.util.List;

import static pl.touk.sputnik.configuration.GeneralOption.*;

public class ProcessorBuilder {

    @NotNull
    public List<ReviewProcessor> buildProcessors() {
        final List<ReviewProcessor> processors = new ArrayList<>();

        if (is(CHECKSTYLE_ENABLED)) {
            processors.add(new CheckstyleProcessor());
        }

        if (is(PMD_ENABLED)) {
            processors.add(new PmdProcessor());
        }

        if (is(FINDBUGS_ENABLED)) {
            processors.add(new FindBugsProcessor());
        }

        if (is(SCALASTYLE_ENABLED)) {
            processors.add(new ScalastyleProcessor());
        }

        if (is(CODE_NARC_ENABLED)) {
            processors.add(new CodeNarcProcessor());
        }

        return processors;
    }

    private static Boolean is(GeneralOption option) {
        return Boolean.valueOf(ConfigurationHolder.instance().getProperty(option));
    }
}
