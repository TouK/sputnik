package pl.touk.sputnik.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CollectorListener implements AuditListener {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorListener.class);

    private List<AuditEvent> errors = new ArrayList<AuditEvent>();
    private List<AuditEvent> exceptions = new ArrayList<AuditEvent>();

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
        LOG.info("Checktyle audit started for {}", auditEvent.getFileName());
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        LOG.info("Checktyle audit finished for {}", auditEvent.getFileName());
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        LOG.info("Error on file {}, line {}, column {}, severity {}: {}",
                auditEvent.getFileName(), auditEvent.getLine(), auditEvent.getColumn(), auditEvent.getSeverityLevel(), auditEvent.getMessage());
        errors.add(auditEvent);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable aThrowable) {
        LOG.warn("Exception on file {}, line {}, column {}: {}",
                auditEvent.getFileName(), auditEvent.getLine(), auditEvent.getColumn(), auditEvent.getMessage());
        exceptions.add(auditEvent);
    }

    public List<AuditEvent> getErrors() {
        return errors;
    }

    public List<AuditEvent> getExceptions() {
        return exceptions;
    }
}
