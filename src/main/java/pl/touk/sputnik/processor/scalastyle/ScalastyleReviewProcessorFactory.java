package pl.touk.sputnik.processor.scalastyle;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class ScalastyleReviewProcessorFactory implements ReviewProcessorFactory<ScalastyleProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.SCALASTYLE_ENABLED));
    }

    @Override
    public ScalastyleProcessor create(Configuration configuration) {
        return new ScalastyleProcessor(configuration);
    }
}
