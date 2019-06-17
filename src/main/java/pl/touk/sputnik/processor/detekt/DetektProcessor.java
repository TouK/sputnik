package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Config;
import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.YamlConfig;
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter;
import io.gitlab.arturbosch.detekt.core.DetektFacade;
import io.gitlab.arturbosch.detekt.core.PathFilter;
import io.gitlab.arturbosch.detekt.core.ProcessingSettings;
import io.gitlab.arturbosch.detekt.core.RuleSetLocator;
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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@AllArgsConstructor
@Slf4j
public class DetektProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "detekt";

    private final Configuration configuration;

    private final ExecutorService executor = ForkJoinPool.commonPool();

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        List<String> files = getFilesForReview(review);
        if (files.isEmpty()) {
            return new ReviewResult();
        }
        String commonPath = new CommonPath(files).find();

        DetektFacade detektFacade = buildDetectFacade(commonPath);

        Detektion detektion = detektFacade.run();

        String commonPathAsFilePrefix = buildCommonPathAsFilePrefix(commonPath);

        return new ResultBuilder(detektion).build(commonPathAsFilePrefix, files);
    }

    @NotNull
    private List<String> getFilesForReview(@NotNull Review review) {
        return review.getFiles(new FileExtensionFilter(Arrays.asList("kt")), new FileNameTransformer());
    }

    @NotNull
    private String buildCommonPathAsFilePrefix(String commonPath) {
        if (commonPath.isEmpty()) {
            return "";
        }
        if (FileSystems.getDefault().getPath(commonPath).toAbsolutePath().toFile().isFile()) {
            return "";
        }
        return commonPath + "/";
    }

    @NotNull
    private DetektFacade buildDetectFacade(String commonPath) {
        String configFilename = configuration.getProperty(GeneralOption.DETEKT_CONFIG_FILE);
        Config config;
        if (configFilename != null) {
            Path configPath = FileSystems.getDefault().getPath(configFilename);
            config = YamlConfig.Companion.load(configPath);
        } else {
            config = loadDefaultConfig();
        }
        ProcessingSettings processingSettings = new ProcessingSettings(
                FileSystems.getDefault().getPath(commonPath),
                config,
                new ArrayList<PathFilter>(),
                false,
                false,
                new ArrayList<Path>(),
                executor,
                System.out,
                System.err
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
}
