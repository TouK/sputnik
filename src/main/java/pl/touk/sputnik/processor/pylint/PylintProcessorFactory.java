package pl.touk.sputnik.processor.pylint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class PylintProcessorFactory implements ReviewProcessorFactory<PylintProcessor> {
    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.PYLINT_ENABLED));
    }

    @Override
    public PylintProcessor create(Configuration configuration) {
        return new PylintProcessor(configuration);
    }
}
