package pl.touk.sputnik.processor.tslint;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TSLintResultParserTest {

    private TSLintResultParser fixture;

    @BeforeEach
    void setUp() {
        fixture = new TSLintResultParser();
    }

    @Test
    void shouldReturnBasicViolationsOnSimpleFunction() throws IOException {
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
    void shouldNotModifyReviewResultWhenNoViolation() {
        // given
        String jsonViolations = "";

        // when
        List<Violation> violations = fixture.parse(jsonViolations);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}
