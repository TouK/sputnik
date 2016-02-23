package pl.touk.sputnik.processor.sonar;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class SonarReviewProcessorFactory implements ReviewProcessorFactory<SonarProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.SONAR_ENABLED));
    }

    @Override
    public SonarProcessor create(Configuration configuration) {
        return new SonarProcessor(configuration);
    }
}
