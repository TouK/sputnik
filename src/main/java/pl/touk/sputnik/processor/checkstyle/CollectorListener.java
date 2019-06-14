package pl.touk.sputnik.processor.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

@Slf4j
public class CollectorListener implements AuditListener {
    @Getter
    private final ReviewResult reviewResult = new ReviewResult();

    @Override
    public void auditStarted(AuditEvent auditEvent) {
        log.info("Checktyle audit started");
    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {
        log.info("Checktyle audit finished");
    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {
        log.debug("Checktyle audit started for {}", auditEvent.getFileName());
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        log.debug("Checktyle audit finished for {}", auditEvent.getFileName());
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        reviewResult.add(new Violation(auditEvent.getFileName(), auditEvent.getLine(), auditEvent.getMessage(), convert(auditEvent.getSeverityLevel())));
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable aThrowable) {
        log.warn("Exception on file {}, line {}, column {}: {}",
                auditEvent.getFileName(), auditEvent.getLine(), auditEvent.getColumn(), auditEvent.getMessage());
    }

    @NotNull
    private Severity convert(SeverityLevel severityLevel) {
        switch (severityLevel) {
            case IGNORE:
                return Severity.IGNORE;
            case INFO:
                return Severity.INFO;
            case WARNING:
                return Severity.WARNING;
            case ERROR:
                return Severity.ERROR;
            default:
                throw new IllegalArgumentException("Severity " + severityLevel + " is not supported");
        }
    }
}
