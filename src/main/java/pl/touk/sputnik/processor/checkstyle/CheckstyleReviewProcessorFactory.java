package pl.touk.sputnik.processor.checkstyle;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class CheckstyleReviewProcessorFactory implements ReviewProcessorFactory<CheckstyleProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.CHECKSTYLE_ENABLED));
    }

    @Override
    public CheckstyleProcessor create(Configuration configuration) {
        return new CheckstyleProcessor(configuration);
    }
}
