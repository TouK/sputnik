package pl.touk.sputnik.processor.jshint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class JsHintReviewProcessorFactory implements ReviewProcessorFactory<JsHintProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.JSHINT_ENABLED));
    }

    @Override
    public JsHintProcessor create(Configuration configuration) {
        return new JsHintProcessor(configuration);
    }
}
