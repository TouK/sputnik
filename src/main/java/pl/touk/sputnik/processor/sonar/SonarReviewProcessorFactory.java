package pl.touk.sputnik.processor.sonar;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class SonarReviewProcessorFactory implements ReviewProcessorFactory<SonarProcessor> {

    @Override
    public boolean isEnabled(Configuration aConfiguration) {
        return Boolean.valueOf(aConfiguration.getProperty(GeneralOption.SONAR_ENABLED));
    }

    @Override
    public SonarProcessor create(Configuration aConfiguration) {
        return new SonarProcessor(aConfiguration);
    }
}
