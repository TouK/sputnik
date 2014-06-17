package pl.touk.sputnik.processor.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

@Slf4j
public class CollectorBugReporter extends AbstractBugReporter {

    @Getter
    private final ReviewResult reviewResult = new ReviewResult();
    private String lastObservedClass;

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        reviewResult.add(new Violation(lastObservedClass, bugInstance.getPrimarySourceLineAnnotation().getStartLine(), bugInstance.getMessage(), convert(bugInstance.getPriority())));
    }

    @Override
    public void reportAnalysisError(AnalysisError error) {
        log.warn("Analysis error {}", error.getMessage(), error.getExceptionMessage());
    }

    @Override
    public void reportMissingClass(String missingClass) {
        //do nothing
    }

    @Override
    public void finish() {
        log.info("FindBugs audit finished");

    }

    @Override
    public BugCollection getBugCollection() {
        log.debug("getBugCollection");
        return null;
    }

    @Override
    public void observeClass(@NotNull ClassDescriptor classDescriptor) {
        log.debug("Observe class {}", classDescriptor.getDottedClassName());
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
