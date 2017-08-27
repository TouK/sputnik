package pl.touk.sputnik.processor.codenarc;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class CodeNarcReviewProcessorFactory implements ReviewProcessorFactory<CodeNarcProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.CODE_NARC_ENABLED));
    }

    @Override
    public CodeNarcProcessor create(Configuration configuration) {
        return new CodeNarcProcessor(configuration);
    }
}
