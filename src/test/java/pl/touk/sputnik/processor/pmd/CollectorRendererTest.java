package pl.touk.sputnik.processor.pmd;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollectorRendererTest extends TestEnvironment {

    private CollectorRenderer renderer;

    private static final String VIOLATION_DESCRIPTION = "this is bug!";
    private static final String RULE_DESCRIPTION = "...and should be fixed";
    private static final String EXTERNAL_INFO_URL = "www.solution.tip";
    private static final String DESCRIPTION_WITH_DETAILS = Joiner.on('\n').join(VIOLATION_DESCRIPTION, RULE_DESCRIPTION, EXTERNAL_INFO_URL);

    @Test
    void shouldReportViolationWithDetails() throws IOException {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.PMD_SHOW_VIOLATION_DETAILS.getKey(), "true"));
        Rule rule = createRule(RULE_DESCRIPTION, EXTERNAL_INFO_URL, RulePriority.HIGH, config);
        Report report = createReportWithVolation(createRuleViolation(rule, VIOLATION_DESCRIPTION, config), config);

        renderer.renderFileReport(report);

        Violation violation = renderer.getReviewResult().getViolations().get(0);
        assertThat(violation.getMessage()).isEqualTo(DESCRIPTION_WITH_DETAILS);
        assertThat(violation.getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    void shouldReportViolationWithoutDetails() throws IOException {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.PMD_SHOW_VIOLATION_DETAILS.getKey(), "false"));
        Rule rule = createRule(RULE_DESCRIPTION, EXTERNAL_INFO_URL, RulePriority.MEDIUM, config);
        Report report = createReportWithVolation(createRuleViolation(rule, VIOLATION_DESCRIPTION, config), config);

        renderer.renderFileReport(report);

        Violation violation = renderer.getReviewResult().getViolations().get(0);
        assertThat(violation.getMessage()).isEqualTo(VIOLATION_DESCRIPTION);
    }

    @NotNull
    private Rule createRule(String ruleDescription, String externalInfoUrl, RulePriority priority, @NotNull Configuration config) {
        renderer = new CollectorRenderer(config);
        Rule rule = mock(Rule.class);
        when(rule.getDescription()).thenReturn(ruleDescription);
        when(rule.getExternalInfoUrl()).thenReturn(externalInfoUrl);
        when(rule.getPriority()).thenReturn(priority);

        return rule;
    }

    @NotNull
    private RuleViolation createRuleViolation(@NotNull Rule rule, String violationDescription, @NotNull Configuration config) {
        renderer = new CollectorRenderer(config);
        RuleViolation violation = mock(RuleViolation.class);
        when(violation.getRule()).thenReturn(rule);
        when(violation.getDescription()).thenReturn(violationDescription);

        return violation;
    }

    @NotNull
    private Report createReportWithVolation(@NotNull RuleViolation violation, @NotNull Configuration config) {
        renderer = new CollectorRenderer(config);
        Report report = mock(Report.class);
        List<RuleViolation> list = new ArrayList<RuleViolation>();
        list.add(violation);
        when(report.iterator()).thenReturn(list.iterator());

        return report;
    }
}
