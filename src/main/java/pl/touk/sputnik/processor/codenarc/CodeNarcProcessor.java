package pl.touk.sputnik.processor.codenarc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenarc.CodeNarcRunner;
import org.codenarc.results.Results;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.GroovyFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class CodeNarcProcessor implements ReviewProcessor {

    private static final String PROCESSOR_NAME = "CodeNarc";

    private final CodeNarcRunnerBuilder codeNarcRunnerBuilder = new CodeNarcRunnerBuilder();
    private final ResultParser resultParser = new ResultParser();
    private final Configuration configuration;

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        List<String> reviewFiles = review.getFiles(new GroovyFilter(), new FileNameTransformer());
        if (noFilesToReview(reviewFiles)) {
            return new ReviewResult();
        }
        CodeNarcRunner codeNarcRunner = codeNarcRunnerBuilder.prepareCodeNarcRunner(reviewFiles, configuration);
        Results results = codeNarcRunner.execute();
        return resultParser.parseResults(results);
    }

    private boolean noFilesToReview(List<String> reviewFiles) {
        return reviewFiles.isEmpty();
    }


    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
