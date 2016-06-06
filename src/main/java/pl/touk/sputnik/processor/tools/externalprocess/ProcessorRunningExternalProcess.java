package pl.touk.sputnik.processor.tools.externalprocess;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.util.List;

public abstract class ProcessorRunningExternalProcess implements ReviewProcessor {

    @Override
    @NotNull
    public ReviewResult process(@NotNull Review review) {
        ReviewResult result = new ReviewResult();
        List<File> files = review.getFiles(getReviewFileFilter(), new IOFileTransformer());
        for (File file : files) {
            for (Violation violation : getParser().parse(processFileAndDumpOutput(file))) {
                result.add(violation);
            }
        }
        return result;
    }

    public abstract FileFilter getReviewFileFilter();

    public abstract ExternalProcessResultParser getParser();

    public abstract String processFileAndDumpOutput(File fileToReview);
}
