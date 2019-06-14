package pl.touk.sputnik.processor.pylint;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PylintResultParserTest {

    private PylintResultParser pylintResultParser;

    @BeforeEach
    void setUp() {
        pylintResultParser = new PylintResultParser();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "pylint/sample-output.txt",
            "pylint/sample-output-no-header.txt"
    })
    void shouldParseSampleViolations(String filePath) throws IOException, URISyntaxException {
        String response = IOUtils.toString(Resources.getResource(filePath).toURI());

        List<Violation> violations = pylintResultParser.parse(response);

        assertThat(violations).hasSize(8);
        assertThat(violations).extracting("filenameOrJavaClassName").contains("PythonTest.py");
        assertThat(violations)
                .extracting("message")
                .contains("Invalid argument name \"n\" [invalid-name]")
                .contains("Black listed name \"bar\" [blacklisted-name]")
                .contains("Black listed name \"foo\" [blacklisted-name]");
    }

    @Test
    void shouldParseMessageTypes() throws IOException, URISyntaxException {
        String response = IOUtils.toString(Resources.getResource("pylint/output-with-many-message-types.txt").toURI());

        List<Violation> violations = pylintResultParser.parse(response);

        assertThat(violations).hasSize(4);
        assertThat(violations)
                .extracting("severity")
                .containsExactly(Severity.INFO, Severity.INFO,  Severity.WARNING, Severity.ERROR);
    }

    @Test
    void shouldNotFailOnMessageId() throws IOException, URISyntaxException {
        String response = IOUtils.toString(Resources.getResource("pylint/output-with-message-id.txt").toURI());

        List<Violation> violations = pylintResultParser.parse(response);

        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting("severity")
                .containsExactly(Severity.INFO);
        assertThat(violations)
                .extracting("message")
                .containsExactly("Missing module docstring [C0111: missing-docstring]");
    }

    @Test
    void shouldThrowExceptionWhenFatalPylintErrorOccurs() throws IOException, URISyntaxException {
        String response = IOUtils.toString(Resources.getResource("pylint/output-with-fatal.txt").toURI());

        Throwable thrown = catchThrowable(() -> pylintResultParser.parse(response));

        assertThat(thrown).isInstanceOf(PylintException.class)
                .hasMessageStartingWith("Fatal error from pylint");
    }
}
