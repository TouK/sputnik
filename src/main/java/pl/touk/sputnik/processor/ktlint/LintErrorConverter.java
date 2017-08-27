package pl.touk.sputnik.processor.ktlint;

import com.github.shyiko.ktlint.core.LintError;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

@RequiredArgsConstructor
class LintErrorConverter implements Function1<LintError, Unit> {

    private final ReviewResult result;
    private final String filePath;

    public Unit invoke(LintError e) {
        result.add(fromLintError(e, filePath));
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
