package pl.touk.sputnik.processor.pylint;

import junitparams.Parameters;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PylintResultParserTest {

    private PylintResultParser pylintResultParser;

    @Before
    public void setUp() {
        pylintResultParser = new PylintResultParser();
    }

    @Test
    @Parameters({
            "/pylint/sample-output.txt",
            "/pylint/sample-output-no-header.txt"
    })
    public void shouldParseSampleViolations() throws IOException {
        // given
        String response = IOUtils.toString(getClass().getResourceAsStream("/pylint/sample-output.txt"));

        // when
        List<Violation> violations = pylintResultParser.parse(response);

        // then
        assertThat(violations).hasSize(8);
        assertThat(violations).are(problemWithFile("PythonTest.py"));
    }

    private Condition<Violation> problemWithFile(final String fileName) {
        return new Condition<Violation>() {
            @Override
            public boolean matches(Violation value) {
                return value.getFilenameOrJavaClassName().equals(fileName);
            }
        };
    }
}