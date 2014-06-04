package pl.touk.sputnik.lint;

import com.android.tools.lint.checks.BuiltinIssueRegistry;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.client.api.LintDriver;
import com.android.tools.lint.client.api.LintRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

public class LintProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "Lint";

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        CollectorLintClient collectorLintClient = new CollectorLintClient();
        IssueRegistry issueRegistry = new BuiltinIssueRegistry();
        LintDriver lintDriver = new LintDriver(issueRegistry, collectorLintClient);
        lintDriver.analyze(new LintRequest(collectorLintClient, review.getIOFiles()));
        return collectorLintClient.getReviewResult();
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
