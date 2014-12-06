package pl.touk.sputnik.processor.pmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import com.google.common.collect.ImmutableMap;

public class CollectorRendererTest {

    private CollectorRenderer renderer = new CollectorRenderer();

    private final String violationDescription = "this is bug!";
    private final String ruleDescription = "...and should be fixed";
    private final String externalInfoUrl = "www.solution.tip";

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("test.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    @Test
    public void shouldReportViolationWithDetails() throws IOException {
        new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.PMD_VIOLATIONS_DETAILS.getKey(), "true"));
        renderer.getReviewResult().getViolations().clear();

        Rule rule = createRule(ruleDescription, externalInfoUrl, RulePriority.HIGH);
        Report report = createReportWithVolation(createRuleViolation(rule, violationDescription));

        renderer.renderFileReport(report);
        Violation violation = renderer.getReviewResult().getViolations().get(0);

        StringBuilder expectedMessage = new StringBuilder();
        expectedMessage.append(violationDescription).append("\n");
        expectedMessage.append(ruleDescription).append("\n");
        expectedMessage.append(externalInfoUrl);

        assertThat(violation.getMessage()).isEqualTo(expectedMessage.toString());
        assertThat(violation.getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    public void shouldReportViolationWithOutDetails() throws IOException {
        new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.PMD_VIOLATIONS_DETAILS.getKey(), "false"));
        renderer.getReviewResult().getViolations().clear();

        Rule rule = createRule(ruleDescription, externalInfoUrl, RulePriority.MEDIUM);
        Report report = createReportWithVolation(createRuleViolation(rule, violationDescription));

        renderer.renderFileReport(report);
        Violation violation = renderer.getReviewResult().getViolations().get(0);

        assertThat(violation.getMessage()).isEqualTo(violationDescription);
    }

    @NotNull
    private Rule createRule(String ruleDescription, String externalInfoUrl, RulePriority priority) {
        Rule rule = mock(Rule.class);
        when(rule.getDescription()).thenReturn(ruleDescription);
        when(rule.getExternalInfoUrl()).thenReturn(externalInfoUrl);
        when(rule.getPriority()).thenReturn(priority);

        return rule;
    }

    @NotNull
    private RuleViolation createRuleViolation(@NotNull Rule rule, String violationDescription) {
        RuleViolation violation = mock(RuleViolation.class);
        when(violation.getRule()).thenReturn(rule);
        when(violation.getDescription()).thenReturn(violationDescription);

        return violation;
    }

    @NotNull
    private Report createReportWithVolation(@NotNull RuleViolation violation) {
        Report report = mock(Report.class);
        List<RuleViolation> list = new ArrayList<RuleViolation>();
        list.add(violation);
        when(report.iterator()).thenReturn(list.iterator());

        return report;
    }
}
