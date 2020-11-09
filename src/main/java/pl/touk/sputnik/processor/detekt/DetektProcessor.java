package pl.touk.sputnik.processor.detekt;

import io.github.detekt.tooling.api.AnalysisResult;
import io.github.detekt.tooling.api.Detekt;
import io.github.detekt.tooling.api.DetektProvider;
import io.github.detekt.tooling.api.MaxIssuesReached;
import io.github.detekt.tooling.api.spec.ProcessingSpec;
import kotlin.Unit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.FileExtensionFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class DetektProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "detekt";

    private final Configuration configuration;

    private final ExecutorService executor = ForkJoinPool.commonPool();

    private final PrintStream printStream = buildPrintStream();

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        List<String> files = getFilesForReview(review);
        if (files.isEmpty()) {
            return new ReviewResult();
        }
        Detekt detekt = buildDetectFacade(files);

        AnalysisResult result = detekt.run();
        if (result.getError() != null) {
            if (result.getError() instanceof MaxIssuesReached) {
                log.info("Detekt reached issues threshold: {}", result.getError().getMessage());
            } else {
                log.error("Detekt run resulted in an error", result.getError());
            }
        }
        return new ResultBuilder(result).build(files);
    }

    @NotNull
    private List<String> getFilesForReview(@NotNull Review review) {
        return review.getFiles(new FileExtensionFilter(Arrays.asList("kt")), new FileNameTransformer());
    }

    @NotNull
    private Detekt buildDetectFacade(List<String> files) {
        return DetektProvider.Companion.load(DetektProvider.class.getClassLoader()).get(
                ProcessingSpec.Companion.invoke(processingSpecBuilder -> {
                    processingSpecBuilder.logging(loggingSpecBuilder -> {
                        loggingSpecBuilder.setOutputChannel(printStream);
                        loggingSpecBuilder.setErrorChannel(printStream);
                        return Unit.INSTANCE;
                    });
                    processingSpecBuilder.project(projectSpecBuilder -> {
                        projectSpecBuilder.setInputPaths(files.stream().map(Paths::get).collect(Collectors.toList()));
                        return Unit.INSTANCE;
                    });
                    processingSpecBuilder.config(configSpecBuilder -> {
                        configSpecBuilder.setUseDefaultConfig(true);
                        String configFilename = configuration.getProperty(GeneralOption.DETEKT_CONFIG_FILE);
                        configSpecBuilder.setConfigPaths(configFilename == null
                                ? Collections.emptyList()
                                : Collections.singleton(Paths.get(configFilename)));
                        return Unit.INSTANCE;
                    });
                    processingSpecBuilder.execution(executionSpecBuilder -> {
                        executionSpecBuilder.setExecutorService(executor);
                        return Unit.INSTANCE;
                    });
                    return Unit.INSTANCE;
                }));
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }

    private PrintStream buildPrintStream() {
        try {
            File tempFile = File.createTempFile("detekt", "out");
            return new PrintStream(tempFile);
        } catch (IOException e) {
            log.warn("Cannot create output stream for detekt", e);
            return System.out;
        }
    }
}
