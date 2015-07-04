package pl.touk.sputnik.processor.sonar;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.annotations.VisibleForTesting;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;

@Slf4j
public class SonarProcessor implements ReviewProcessor {

    private static final String PROCESSOR_NAME = "Sonar";

    private SonarRunnerBuilder sonarRunnerBuilder;
    private final Configuration configuration;

    public SonarProcessor(@NotNull final Configuration configuration) {
        this(new SonarRunnerBuilder(), configuration);
    }

    public SonarProcessor(SonarRunnerBuilder sonarRunnerBuilder, @NotNull final Configuration configuration) {
        this.sonarRunnerBuilder = sonarRunnerBuilder;
        this.configuration = configuration;
    }

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        if (review.getFiles().isEmpty()) {
            return new ReviewResult();
        }

        try {
            SonarRunner runner = sonarRunnerBuilder.prepareRunner(review, configuration);
            File resultFile = runner.run();
            SonarResultParser parser = new SonarResultParser(resultFile);
            return filterResults(parser.parseResults(), review);
        }
        catch (IOException e) {
            throw new ReviewException("SonarResultParser error", e);
        }
    }

    /**
     * Filters a ReviewResult to keep only the violations that are about a file
     * which is modified by a given review.
     */
    @VisibleForTesting
    ReviewResult filterResults(ReviewResult results, Review review) {
        ReviewResult filteredResults = new ReviewResult();
        Set<String> reviewFiles = new HashSet<>();
        for (ReviewFile file : review.getFiles()) {
            reviewFiles.add(file.getReviewFilename());
        }
        for (Violation violation : results.getViolations()) {
            if (reviewFiles.contains(violation.getFilenameOrJavaClassName())) {
                filteredResults.add(violation);
            }
        }
        return filteredResults;
    }

    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
