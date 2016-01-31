package pl.touk.sputnik.engine;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.processor.ReviewProcessorFactory;
import pl.touk.sputnik.review.ReviewProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ProcessorBuilder {

    @NotNull
    public static List<ReviewProcessor> buildProcessors(Configuration configuration) {
        List<ReviewProcessor> processors = new ArrayList<>();

        ServiceLoader<ReviewProcessorFactory> loader = ServiceLoader.load(ReviewProcessorFactory.class);
        Iterator<ReviewProcessorFactory> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ReviewProcessorFactory factory = iterator.next();
            if (factory.isEnabled(configuration)) {
                processors.add(factory.create(configuration));
            }
        }
        return processors;
    }
}
