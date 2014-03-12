package pl.touk.sputnik.pmd;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectorRenderer extends AbstractRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(CollectorRenderer.class);
    private static final String SPUTNIK_PMD_COLLECT_RENDERER = "Sputnik PMD Collect Renderer";
    private final List<RuleViolation> ruleViolations = new ArrayList<RuleViolation>();

    public CollectorRenderer() {
        super(SPUTNIK_PMD_COLLECT_RENDERER, SPUTNIK_PMD_COLLECT_RENDERER);
    }

    @Override
    public String defaultFileExtension() {
        return null;
    }

    @Override
    public void startFileAnalysis(DataSource dataSource) {
        LOG.info("PMD audit started for {}", dataSource);
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        Iterator<RuleViolation> violations = report.iterator();
        if (violations.hasNext()) {
            RuleViolation ruleViolation = violations.next();
            LOG.debug("Error on file {}, line {}, severity {}: {}", ruleViolation.getFilename(), ruleViolation.getBeginLine(), ruleViolation.getRule().getPriority(), ruleViolation.getRule().getMessage());
            ruleViolations.add(ruleViolation);
        }
    }

    @Override
    public void start() throws IOException {
        LOG.info("PMD audit started");
    }

    @Override
    public void end() throws IOException {
        LOG.info("PMD audit finished");
    }

    public List<RuleViolation> getRuleViolations() {
        return ruleViolations;
    }
}
