package pl.touk.sputnik.processor.jslint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class JsLintReviewProcessorFactory implements ReviewProcessorFactory<JsLintProcessor> {

    @Override
    public boolean isEnabled(Configuration aConfiguration) {
        return Boolean.valueOf(aConfiguration.getProperty(GeneralOption.JSLINT_ENABLED));
    }

    @Override
    public JsLintProcessor create(Configuration aConfiguration) {
        return new JsLintProcessor(aConfiguration);
    }
}
