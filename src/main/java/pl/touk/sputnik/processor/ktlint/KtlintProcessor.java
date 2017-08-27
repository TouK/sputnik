package pl.touk.sputnik.processor.ktlint;

import com.github.shyiko.ktlint.core.KtLint;
import com.github.shyiko.ktlint.core.LintError;
import com.github.shyiko.ktlint.core.RuleSet;
import com.github.shyiko.ktlint.core.RuleSetProvider;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.KotlinFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class KtlintProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "ktlint";

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        ReviewResult result = new ReviewResult();
        List<String> filePaths = review.getFiles(new KotlinFilter(), new FileNameTransformer());
        List<File> files = review.getFiles(new KotlinFilter(), new IOFileTransformer());
        List<RuleSet> ruleSets = new ArrayList<>();
        for (RuleSetProvider ruleSetProvider : ServiceLoader.load(RuleSetProvider.class)) {
            ruleSets.add(ruleSetProvider.get());
        }
        for (int i = 0; i < files.size(); i++) {
            File f = files.get(i);
            String filePath = filePaths.get(i);
            String text = readFile(f);
            KtLint.INSTANCE.lint(text, ruleSets, getLintErrorUnitFunction1(result, filePath));
        }
        return result;
    }

    @NotNull
    private Function1<LintError, Unit> getLintErrorUnitFunction1(final ReviewResult result, final String filePath) {
        return new Function1<LintError, Unit>() {
            @Override
            public Unit invoke(LintError e) {
                result.add(new Violation(filePath, e.getLine(), formatMessage(e), Severity.WARNING));
                return Unit.INSTANCE;
            }
        };
    }

    private String formatMessage(LintError e) {
        return String.format("[%s] %s in column %d", e.getRuleId(), e.getDetail(), e.getCol());
    }

    private String readFile(File file) {
        try {
            return IOUtils.toString(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file " + file.getPath(), e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
