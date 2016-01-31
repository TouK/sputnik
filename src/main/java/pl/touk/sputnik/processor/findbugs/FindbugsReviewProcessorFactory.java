package pl.touk.sputnik.processor.findbugs;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class FindbugsReviewProcessorFactory implements ReviewProcessorFactory<FindBugsProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.FINDBUGS_ENABLED));
    }

    @Override
    public FindBugsProcessor create(Configuration configuration) {
        return new FindBugsProcessor(configuration);
    }
}
