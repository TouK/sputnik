package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.AbstractBugReporter;
import edu.umd.cs.findbugs.AnalysisError;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

public class CollectorBugReporter extends AbstractBugReporter {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorBugReporter.class);
    @Getter
    private final ReviewResult reviewResult = new ReviewResult();
    private String lastObservedClass;

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        reviewResult.add(new Violation(lastObservedClass, bugInstance.getPrimarySourceLineAnnotation().getStartLine(), bugInstance.getMessage(), convert(bugInstance.getPriority())));
    }

    @Override
    public void reportAnalysisError(AnalysisError error) {
        LOG.warn("Analysis error {}", error);
    }

    @Override
    public void reportMissingClass(String missingClass) {
        //do nothing
    }

    @Override
    public void finish() {
        LOG.info("FindBugs audit finished");

    }

    @Override
    public BugCollection getBugCollection() {
        LOG.debug("getBugCollection");
        return null;
    }

    @Override
    public void observeClass(@NotNull ClassDescriptor classDescriptor) {
        LOG.debug("Observe class {}", classDescriptor.getDottedClassName());
        lastObservedClass = classDescriptor.getDottedClassName();
    }

    @NotNull
    private Severity convert(int priority) {
        switch (priority) {
            case Priorities.IGNORE_PRIORITY:
                return Severity.IGNORE;
            case Priorities.EXP_PRIORITY:
            case Priorities.LOW_PRIORITY:
            case Priorities.NORMAL_PRIORITY:
                return Severity.INFO;
            case Priorities.HIGH_PRIORITY:
                return Severity.WARNING;
            default:
                throw new IllegalArgumentException("Priority " + priority + " is not supported");
        }
    }


}
