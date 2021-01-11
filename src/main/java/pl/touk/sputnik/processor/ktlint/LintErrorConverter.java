package pl.touk.sputnik.processor.ktlint;

import com.pinterest.ktlint.core.LintError;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.List;

@RequiredArgsConstructor
class LintErrorConverter implements Function2<LintError, Boolean, Unit> {

    private final ReviewResult result;
    private final String filePath;
    private final List<String> excludedRules;

    @Override
    public Unit invoke(LintError e, Boolean corrected) {
        if (!excludedRules.contains(e.getRuleId())) {
            result.add(fromLintError(e, filePath));
        }
        return Unit.INSTANCE;
    }

    @NotNull
    private Violation fromLintError(LintError e, String filePath) {
        return new Violation(filePath, e.getLine(), formatMessage(e), Severity.WARNING);
    }

    private String formatMessage(LintError e) {
        return String.format("[%s] %s in column %d", e.getRuleId(), e.getDetail(), e.getCol());
    }
}
