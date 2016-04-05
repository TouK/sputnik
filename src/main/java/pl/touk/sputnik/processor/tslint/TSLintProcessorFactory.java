package pl.touk.sputnik.processor.tslint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class TSLintProcessorFactory implements ReviewProcessorFactory<TSLintProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.TSLINT_ENABLED));
    }

    @Override
    public TSLintProcessor create(Configuration configuration) {
        return new TSLintProcessor(configuration);
    }
}
