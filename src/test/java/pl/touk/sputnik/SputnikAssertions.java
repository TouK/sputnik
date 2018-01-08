package pl.touk.sputnik;

import org.assertj.core.api.Assertions;
import pl.touk.sputnik.assertion.ViolationAssert;
import pl.touk.sputnik.review.Violation;

public class SputnikAssertions extends Assertions {

    public static ViolationAssert assertThat(Violation actual) {
        return new ViolationAssert(actual);
    }
}
