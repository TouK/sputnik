package pl.touk.sputnik.processor.jslint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class JsLintReviewProcessorFactory implements ReviewProcessorFactory<JsLintProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.JSLINT_ENABLED));
    }

    @Override
    public JsLintProcessor create(Configuration configuration) {
        return new JsLintProcessor(configuration);
    }
}
