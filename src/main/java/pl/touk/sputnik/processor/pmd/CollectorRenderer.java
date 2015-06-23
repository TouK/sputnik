package pl.touk.sputnik.processor.pmd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class CollectorRenderer extends AbstractRenderer {
    private static final String SPUTNIK_PMD_COLLECT_RENDERER = "Sputnik PMD Collect Renderer";
    private static final char LINE_SEPARATOR = '\n';
    private final Configuration configuration;

    @Getter
    private final ReviewResult reviewResult = new ReviewResult();

    public CollectorRenderer(Properties properties) {
        this(ConfigurationBuilder.initFromProperties(properties));
    }

    public CollectorRenderer(Configuration configuration) {
        super(SPUTNIK_PMD_COLLECT_RENDERER, SPUTNIK_PMD_COLLECT_RENDERER);
        this.configuration = configuration;
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
        boolean showDetails = Boolean.valueOf(configuration.getProperty(GeneralOption.PMD_SHOW_VIOLATION_DETAILS));

        for (RuleViolation ruleViolation : report) {
            String violationDescription = showDetails ? renderViolationDetails(ruleViolation) :ruleViolation.getDescription();
            reviewResult.add(new Violation(ruleViolation.getFilename(), ruleViolation.getBeginLine(), violationDescription, convert(ruleViolation.getRule().getPriority())));
        }
    }

    private String renderViolationDetails(RuleViolation ruleViolation) {
        StringBuilder fullDescription = new StringBuilder(ruleViolation.getDescription());

        String reason = ruleViolation.getRule().getDescription();
        if (StringUtils.isNotEmpty(reason)) {
            fullDescription.append(LINE_SEPARATOR).append(reason);
        }
        String url = ruleViolation.getRule().getExternalInfoUrl();
        if (StringUtils.isNotEmpty(url)) {
            fullDescription.append(LINE_SEPARATOR).append(url);
        }

        return fullDescription.toString();
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
