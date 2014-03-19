package pl.touk.sputnik.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

public class CollectorListener implements AuditListener {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorListener.class);
    private static final String SOURCE_NAME = "Checkstyle";
    @Getter
    private final ReviewResult reviewResult = new ReviewResult(SOURCE_NAME);

    @Override
    public void auditStarted(AuditEvent auditEvent) {
        LOG.info("Checktyle audit started");
    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {
        LOG.info("Checktyle audit finished");
    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {
        LOG.debug("Checktyle audit started for {}", auditEvent.getFileName());
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        LOG.debug("Checktyle audit finished for {}", auditEvent.getFileName());
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        reviewResult.add(new Violation(auditEvent.getFileName(), auditEvent.getLine(), auditEvent.getMessage(), convert(auditEvent.getSeverityLevel())));
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable aThrowable) {
        LOG.warn("Exception on file {}, line {}, column {}: {}",
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
