package pl.touk.sputnik.findbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectorBugReporter extends AbstractBugReporter {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorBugReporter.class);

    private String lastObservedClass;

    private List<BugInstance> bugs = new ArrayList<BugInstance>();
    private Map<String, File> ioFileToJavaClassNames = new HashMap<String, File>();

    @Override
    protected void doReportBug(BugInstance bugInstance) {
        LOG.info("Error on class {}, line {}, severity {}: {}",
                ioFileToJavaClassNames.get(lastObservedClass),
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
        //do nothing
//        LOG.debug("Missing class {}", string);
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

    public List<BugInstance> getBugs() {
        return bugs;
    }


    public void setIoFileToJavaClassNames(Map<String, File> ioFileToJavaClassNames) {
        this.ioFileToJavaClassNames = ioFileToJavaClassNames;
    }
}
