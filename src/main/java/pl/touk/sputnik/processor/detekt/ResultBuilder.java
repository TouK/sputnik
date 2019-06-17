package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.Severity;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
class ResultBuilder {

    private final Detektion detektion;

    @NotNull
    ReviewResult build(String commonPath, List<String> filesForReview) {
        ReviewResult reviewResult = new ReviewResult();
        detektion.getFindings().forEach((ruleSet, value) -> value.forEach(finding -> {
            String filePath = commonPath + finding.getLocation().getFile();
            Optional<String> file = filesForReview.stream().filter(filePath::endsWith).findFirst();
            file.ifPresent(f ->
                    {
                        reviewResult.add(new Violation(f,
                                finding.getLocation().getSource().getLine(),
                                buildMessage(ruleSet, finding.getIssue().getId(), finding.getIssue().getDescription()),
                                mapSeverity(finding.getIssue().getSeverity())));
                    }
            );
        }));

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
