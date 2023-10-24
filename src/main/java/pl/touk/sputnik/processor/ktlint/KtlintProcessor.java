package pl.touk.sputnik.processor.ktlint;

import com.pinterest.ktlint.core.Code;
import com.pinterest.ktlint.core.KtLintRuleEngine;
import com.pinterest.ktlint.core.api.EditorConfigDefaults;
import com.pinterest.ktlint.core.api.EditorConfigOverride;
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class KtlintProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "ktlint";

    private final List<String> excludedRules;
    private final KtLintRuleEngine ktLintRuleEngine;

    KtlintProcessor(Configuration configuration) {
        excludedRules = parseExcludedRules(configuration);
        StandardRuleSetProvider ruleSetProvider = new StandardRuleSetProvider();
        ktLintRuleEngine = new KtLintRuleEngine(
                ruleSetProvider.getRuleProviders(),
                EditorConfigDefaults.Companion.getEMPTY_EDITOR_CONFIG_DEFAULTS(),
                EditorConfigOverride.Companion.getEMPTY_EDITOR_CONFIG_OVERRIDE(),
                false
        );
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
            ktLintRuleEngine.lint(
                    new Code.CodeFile(new File(filePath)),
                    new LintErrorConverter(result, filePath, excludedRules)
            );
        }
        return result;
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
