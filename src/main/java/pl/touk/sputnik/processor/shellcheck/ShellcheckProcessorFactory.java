package pl.touk.sputnik.processor.shellcheck;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class ShellcheckProcessorFactory implements ReviewProcessorFactory<ShellcheckProcessor> {
    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.SHELLCHECK_ENABLED));
    }

    @Override
    public ShellcheckProcessor create(Configuration configuration) {
        return new ShellcheckProcessor(configuration);
    }
}
