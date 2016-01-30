package pl.touk.sputnik.processor.pmd;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class PmdReviewProcessorFactory implements ReviewProcessorFactory<PmdProcessor> {

    @Override
    public boolean isEnabled(Configuration aConfiguration) {
        return Boolean.valueOf(aConfiguration.getProperty(GeneralOption.PMD_ENABLED));
    }

    @Override
    public PmdProcessor create(Configuration aConfiguration) {
        return new PmdProcessor(aConfiguration);
    }
}