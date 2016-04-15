package pl.touk.sputnik.processor.pylint;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.PythonFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;

@Slf4j
public class PylintProcessor implements ReviewProcessor {

    private PylintExecutor pylintExecutor;
    private PylintResultParser pylintResultParser;

    public PylintProcessor(Configuration configuration) {
        pylintExecutor = new PylintExecutor(configuration.getProperty(GeneralOption.PYLINT_RCFILE));
        pylintResultParser = new PylintResultParser();
    }

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        ReviewResult reviewResult = new ReviewResult();
        for (File file : review.getFiles(new PythonFilter(), new IOFileTransformer())) {
            for (Violation violation : pylintResultParser.parse(pylintExecutor.runOnFile(file.getAbsolutePath()))) {
                reviewResult.add(violation);
            }
        }
        return reviewResult;
    }

    @NotNull
    @Override
    public String getName() {
        return "Pylint";
    }
}
