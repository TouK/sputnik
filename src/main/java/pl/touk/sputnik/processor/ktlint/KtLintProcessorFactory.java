package pl.touk.sputnik.processor.ktlint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class KtLintProcessorFactory implements ReviewProcessorFactory<KtlintProcessor> {
    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.parseBoolean(configuration.getProperty(GeneralOption.KTLINT_ENABLED));
    }

    @Override
    public KtlintProcessor create(Configuration configuration) {
        return new KtlintProcessor(configuration);
    }
}
