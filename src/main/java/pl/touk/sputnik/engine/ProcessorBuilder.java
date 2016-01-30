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

        ServiceLoader<ReviewProcessorFactory> theLoader = ServiceLoader.load(ReviewProcessorFactory.class);
        Iterator<ReviewProcessorFactory> theIterator = theLoader.iterator();
        while (theIterator.hasNext()) {
            ReviewProcessorFactory theFactory = theIterator.next();
            if (theFactory.isEnabled(configuration)) {
                processors.add(theFactory.create(configuration));
            }
        }
        return processors;
    }
}
