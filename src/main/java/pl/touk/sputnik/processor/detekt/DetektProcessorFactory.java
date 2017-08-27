package pl.touk.sputnik.processor.detekt;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class DetektProcessorFactory implements ReviewProcessorFactory<DetektProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.DETEKT_ENABLED));
    }

    @Override
    public DetektProcessor create(Configuration configuration) {
        return new DetektProcessor(configuration);
    }
}
