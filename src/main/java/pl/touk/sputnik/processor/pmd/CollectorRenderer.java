package pl.touk.sputnik.processor.pmd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;

@Slf4j
public class CollectorRenderer extends AbstractRenderer {
    private static final String SPUTNIK_PMD_COLLECT_RENDERER = "Sputnik PMD Collect Renderer";

    @Getter
    private final ReviewResult reviewResult = new ReviewResult();

    public CollectorRenderer() {
        super(SPUTNIK_PMD_COLLECT_RENDERER, SPUTNIK_PMD_COLLECT_RENDERER);
    }

    @Override
    public String defaultFileExtension() {
        return null;
    }

    @Override
    public void startFileAnalysis(DataSource dataSource) {
        log.debug("PMD audit started for {}", dataSource);
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        for (RuleViolation ruleViolation : report) {
            reviewResult.add(new Violation(ruleViolation.getFilename(), ruleViolation.getBeginLine(), ruleViolation.getDescription(), convert(ruleViolation.getRule().getPriority())));
        }
    }

    @Override
    public void start() throws IOException {
        log.info("PMD audit started");
    }

    @Override
    public void end() throws IOException {
        log.info("PMD audit finished");
    }

    @NotNull
    private Severity convert(@NotNull RulePriority rulePriority) {
        switch (rulePriority) {
            case HIGH:
                return Severity.ERROR;
            case MEDIUM_HIGH:
                return Severity.WARNING;
            case MEDIUM:
                return Severity.INFO;
            case MEDIUM_LOW:
                return Severity.INFO;
            case LOW:
                return Severity.IGNORE;
            default:
                throw new IllegalArgumentException("RulePriority " + rulePriority + " is not supported");
        }
    }
}
