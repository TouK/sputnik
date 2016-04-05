package pl.touk.sputnik.processor.tslint;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TSLintResultParserTest {

    private TSLintResultParser fixture;

    @Before
    public void setUp() {
        fixture = new TSLintResultParser();
    }

    @Test
    public void shouldReturnBasicViolationsOnSimpleFunction() throws IOException {
        // given
        String jsonResponse = IOUtils.toString(getClass().getResourceAsStream("/json/tslint-results.json"));

        // when
        List<Violation> violations = fixture.parse(jsonResponse);

        // then
        assertThat(violations).hasSize(1);

        Violation violation = violations.get(0);
        assertThat(violation.getMessage()).isEqualTo("unused variable: 'greeter'");
        assertThat(violation.getLine()).isEqualTo(10);
        assertThat(violation.getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    public void shouldNotModifyReviewResultWhenNoViolation() {
        // given
        ReviewResult reviewResult = new ReviewResult();
        String jsonViolations = "";

        // when
        List<Violation> violations = fixture.parse(jsonViolations);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}
