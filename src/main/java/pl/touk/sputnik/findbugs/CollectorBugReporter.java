package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.Severity;

public class CollectorBugReporter extends AbstractBugReporter {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorBugReporter.class);
    private static final String SOURCE_NAME = "FindBugs";
    private final Review review;
    private String lastObservedClass;

    public CollectorBugReporter(@NotNull Review review, @NotNull String source) {
        this.review = review;
    }

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        review.addErrorOnJavaClassName(lastObservedClass, SOURCE_NAME,
                bugInstance.getPrimarySourceLineAnnotation().getStartLine(),
                bugInstance.getMessage(),
                convert(bugInstance.getPriority()));
    }

    @Override
    public void reportAnalysisError(AnalysisError error) {
        LOG.warn("Analysis error {}", error);
    }

    @Override
    public void reportMissingClass(String string) {
        //do nothing
    }

    public void finish() {
        LOG.info("FindBugs audit finished");

    }

    public BugCollection getBugCollection() {
        LOG.info("getBugCollection");
        return null;
    }

    public void observeClass(ClassDescriptor classDescriptor) {
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
