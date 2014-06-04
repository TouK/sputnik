package pl.touk.sputnik.lint;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.tools.lint.EcjParser;
import com.android.tools.lint.LintCliXmlParser;
import com.android.tools.lint.client.api.IDomParser;
import com.android.tools.lint.client.api.IJavaParser;
import com.android.tools.lint.client.api.LintClient;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Severity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;

import java.io.File;
import java.io.IOException;

@Slf4j
public class CollectorLintClient extends LintClient {
    @Getter
    private final ReviewResult reviewResult = new ReviewResult();

    @Override
    public void report(@NonNull Context context, @NonNull Issue issue, @NonNull Severity severity, @Nullable Location location, @NonNull String message, @Nullable Object data) {
        reviewResult.add(new Violation(location.getFile().getName(), location.getStart().getLine(), message, convert(severity)));
    }

    @Override
    public void log(@NonNull Severity severity, @Nullable Throwable exception, @Nullable String format, @Nullable Object... args) {
        log.info("Lint processor logged exception {} with severity {} and message {}", exception, severity, String.format(format, args));
    }

    @Override
    public IDomParser getDomParser() {
        return new LintCliXmlParser();
    }

    @Override
    public IJavaParser getJavaParser() {
        return new EcjParser(this);
    }

    @Override
    public String readFile(@NonNull File file) {
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException("Read file " + file + " failed", e);
        }
    }

    @NotNull
    private pl.touk.sputnik.review.Severity convert(Severity severity) {
        switch (severity) {
            case FATAL:
            case ERROR:
                return pl.touk.sputnik.review.Severity.ERROR;
            case WARNING:
                return pl.touk.sputnik.review.Severity.WARNING;
            case INFORMATIONAL:
                return pl.touk.sputnik.review.Severity.INFO;
            case IGNORE:
                return pl.touk.sputnik.review.Severity.IGNORE;
            default:
                throw new IllegalArgumentException("Severity " + severity + " is not supported");
        }
    }
}
