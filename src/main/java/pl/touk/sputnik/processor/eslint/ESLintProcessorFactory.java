package pl.touk.sputnik.processor.eslint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class ESLintProcessorFactory implements ReviewProcessorFactory<ESLintProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.ESLINT_ENABLED));
    }

    @Override
    public ESLintProcessor create(Configuration configuration) {
        return new ESLintProcessor(configuration);
    }
}