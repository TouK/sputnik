package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Config;
import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig;
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter;
import io.gitlab.arturbosch.detekt.core.DetektFacade;
import io.gitlab.arturbosch.detekt.core.ProcessingSettings;
import io.gitlab.arturbosch.detekt.core.RuleSetLocator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.config.JvmTarget;
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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
        DetektFacade detektFacade = buildDetectFacade(files);

        Detektion detektion = detektFacade.run();

        return new ResultBuilder(detektion).build(files);
    }

    @NotNull
    private List<String> getFilesForReview(@NotNull Review review) {
        return review.getFiles(new FileExtensionFilter(Arrays.asList("kt")), new FileNameTransformer());
    }

    @NotNull
    private DetektFacade buildDetectFacade(List<String> files) {
        String configFilename = configuration.getProperty(GeneralOption.DETEKT_CONFIG_FILE);
        Config config;
        FileSystem fileSystem = FileSystems.getDefault();
        if (configFilename != null) {
            Path configPath = fileSystem.getPath(configFilename);
            config = YamlConfig.Companion.load(configPath);
        } else {
            config = loadDefaultConfig();
        }
        ProcessingSettings processingSettings = new ProcessingSettings(
                files.stream().map(f -> fileSystem.getPath(f)).collect(Collectors.toList()),
                config,
                null,
                false,
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                JvmTarget.DEFAULT,
                executor,
                printStream,
                printStream,
                false,
                false,
                new ArrayList<>()
        );

        return DetektFacade.Companion.create(processingSettings, new RuleSetLocator(processingSettings).load(), Arrays.asList(new LoggingFileProcessor()));
    }

    @NotNull
    private Config loadDefaultConfig() {
        return YamlConfig.Companion.loadResource(new ClasspathResourceConverter().convert("default-detekt-config.yml"));
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
