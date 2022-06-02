package pl.touk.sputnik.processor.ktlint;

import com.pinterest.ktlint.core.KtLint;
import com.pinterest.ktlint.core.RuleSet;
import com.pinterest.ktlint.core.RuleSetProvider;
import com.pinterest.ktlint.core.api.EditorConfigOverride;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.KotlinFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

class KtlintProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "ktlint";

    private final List<RuleSet> ruleSets;
    private final List<String> excludedRules;

    KtlintProcessor(Configuration configuration) {
        excludedRules = parseExcludedRules(configuration);
        ruleSets = findRuleSets();
    }

    private List<RuleSet> findRuleSets() {
        List<RuleSet> ruleSets = new ArrayList<>();
        for (RuleSetProvider ruleSetProvider : ServiceLoader.load(RuleSetProvider.class)) {
            ruleSets.add(ruleSetProvider.get());
        }
        return ruleSets;
    }

    @NotNull
    private List<String> parseExcludedRules(Configuration configuration) {
        String excludeProperty = configuration.getProperty(GeneralOption.KTLINT_EXCLUDE);
        if (excludeProperty == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(excludeProperty.split(","));
    }

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        List<String> filePaths = review.getFiles(new KotlinFilter(), new FileNameTransformer());
        return processFiles(filePaths);
    }

    private ReviewResult processFiles(List<String> filePaths) {
        ReviewResult result = new ReviewResult();
        for (String filePath : filePaths) {
            String text = readFile(filePath);
            KtLint.INSTANCE.lint(new KtLint.ExperimentalParams(
                    null,
                    text,
                    ruleSets,
                    Collections.emptyMap(),
                    new LintErrorConverter(result, filePath, excludedRules),
                    false,
                    null,
                    false,
                    EditorConfigOverride.Companion.getEmptyEditorConfigOverride(),
                    false
                    )
            );
        }
        return result;
    }

    private String readFile(String filePath) {
        try {
            return IOUtils.toString(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + filePath, e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
