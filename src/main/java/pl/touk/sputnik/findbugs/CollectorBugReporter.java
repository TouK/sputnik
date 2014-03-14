package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.AbstractBugReporter;
import edu.umd.cs.findbugs.AnalysisError;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CollectorBugReporter extends AbstractBugReporter {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorBugReporter.class);

    private List<BugInstance> bugs = new ArrayList<BugInstance>();

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        LOG.info("Error on class {}, line {}, severity {}: {}",
                bugInstance.getType(),
                bugInstance.getPrimarySourceLineAnnotation().getStartLine(),
                bugInstance.getPriority(),
                bugInstance.getMessage());
        bugs.add(bugInstance);
    }

    @Override
    public void reportAnalysisError(AnalysisError error) {
        LOG.warn("Analysis error {}", error);
    }

    @Override
    public void reportMissingClass(String string) {
        LOG.warn("Missing class {}", string);
    }

    public void finish() {
        LOG.info("FindBugs audit finished");

    }

    public BugCollection getBugCollection() {
        LOG.info("getBugCollection");
        return null;
    }

    public void observeClass(ClassDescriptor classDescriptor) {
        LOG.info("observeClass {}", classDescriptor);
    }

    public List<BugInstance> getBugs() {
        return bugs;
    }
}
