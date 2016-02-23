package pl.touk.sputnik.processor.pmd;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class PmdReviewProcessorFactory implements ReviewProcessorFactory<PmdProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.PMD_ENABLED));
    }

    @Override
    public PmdProcessor create(Configuration configuration) {
        return new PmdProcessor(configuration);
    }
}