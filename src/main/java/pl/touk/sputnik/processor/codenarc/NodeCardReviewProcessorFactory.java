package pl.touk.sputnik.processor.codenarc;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class NodeCardReviewProcessorFactory implements ReviewProcessorFactory<CodeNarcProcessor> {

    @Override
    public boolean isEnabled(Configuration aConfiguration) {
        return Boolean.valueOf(aConfiguration.getProperty(GeneralOption.CODE_NARC_ENABLED));
    }

    @Override
    public CodeNarcProcessor create(Configuration aConfiguration) {
        return new CodeNarcProcessor(aConfiguration);
    }
}
