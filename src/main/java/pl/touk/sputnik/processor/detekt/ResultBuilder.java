package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.Finding;
import io.gitlab.arturbosch.detekt.api.Severity;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
class ResultBuilder {

    private final Detektion detektion;

    @NotNull
    ReviewResult build(String commonPath, List<String> filesForReview) {
        ReviewResult reviewResult = new ReviewResult();

        for (Map.Entry<String, List<Finding>> findings : detektion.getFindings().entrySet()) {
            String ruleSet = findings.getKey();
            for (Finding finding : findings.getValue()) {
                String filePath = commonPath + finding.getLocation().getFile();
                if (!filesForReview.contains(filePath)) {
                    continue;
                }
                reviewResult.add(new Violation(filePath,
                        finding.getLocation().getSource().getLine(),
                        buildMessage(ruleSet, finding.getIssue().getId(), finding.getIssue().getDescription()),
                        mapSeverity(finding.getIssue().getSeverity())));
            }
        }

        return reviewResult;
    }

    private String buildMessage(String ruleSet, String ruleId, String description) {
        return String.format("[%s/%s] %s", ruleSet, ruleId, description);
    }

    private pl.touk.sputnik.review.Severity mapSeverity(Severity severity) {

        switch (severity) {
            case Minor:
            case Style:
            case Maintainability:
                return pl.touk.sputnik.review.Severity.INFO;
            case Warning:
            case Security:
            case CodeSmell:
            case Performance:
                return pl.touk.sputnik.review.Severity.WARNING;
            case Defect:
                return pl.touk.sputnik.review.Severity.ERROR;
            default:
                return pl.touk.sputnik.review.Severity.ERROR;
        }
    }
}
