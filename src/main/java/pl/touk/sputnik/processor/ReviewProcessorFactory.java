package pl.touk.sputnik.processor;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.ReviewProcessor;

public interface ReviewProcessorFactory<T extends ReviewProcessor> {

    boolean isEnabled(Configuration aConfiguration);

    T create(Configuration aConfiguration);
}
