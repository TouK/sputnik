package pl.touk.sputnik.processor.spotbugs;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class SpotBugsReviewProcessorFactory implements ReviewProcessorFactory<SpotBugsProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.SPOTBUGS_ENABLED)) ||
                Boolean.valueOf(configuration.getProperty(GeneralOption.FINDBUGS_ENABLED));
    }

    @Override
    public SpotBugsProcessor create(Configuration configuration) {
        return new SpotBugsProcessor(configuration);
    }
}
