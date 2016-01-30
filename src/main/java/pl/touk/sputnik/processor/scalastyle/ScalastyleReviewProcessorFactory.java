package pl.touk.sputnik.processor.scalastyle;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class ScalastyleReviewProcessorFactory implements ReviewProcessorFactory<ScalastyleProcessor> {

    @Override
    public boolean isEnabled(Configuration aConfiguration) {
        return Boolean.valueOf(aConfiguration.getProperty(GeneralOption.SCALASTYLE_ENABLED));
    }

    @Override
    public ScalastyleProcessor create(Configuration aConfiguration) {
        return new ScalastyleProcessor(aConfiguration);
    }
}
