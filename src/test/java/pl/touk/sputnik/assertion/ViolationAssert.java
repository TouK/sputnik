package pl.touk.sputnik.assertion;

import org.assertj.core.api.AbstractAssert;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import static org.assertj.core.api.Assertions.assertThat;

public class ViolationAssert extends AbstractAssert<ViolationAssert, Violation> {

    public ViolationAssert(Violation actual) {
        super(actual, ViolationAssert.class);
    }

    public ViolationAssert hasLine(int line) {
        isNotNull();
        assertThat(actual.getLine()).isEqualTo(line);
        return this;
    }

    public ViolationAssert hasMessage(String message) {
        isNotNull();
        assertThat(actual.getMessage()).isEqualTo(message);
        return this;
    }

    public ViolationAssert hasSeverity(Severity severity) {
        isNotNull();
        assertThat(actual.getSeverity()).isEqualTo(severity);
        return this;
    }
}