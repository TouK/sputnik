package pl.touk.sputnik.processor.shellcheck;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ShellcheckResultParserTest {

    private ShellcheckResultParser shellcheckResultParser;

    @BeforeEach
    public void setUp() {
        shellcheckResultParser = new ShellcheckResultParser();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "shellcheck/empty-file.txt",
            "shellcheck/no-violations.txt"
    })
    void shouldParseNoViolations(String filePath) throws IOException, URISyntaxException {
        // given
        String response = IOUtils.toString(Resources.getResource(filePath).toURI());

        // when
        List<Violation> violations = shellcheckResultParser.parse(response);

        // then
        assertThat(violations).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "shellcheck/sample-output.txt"
    })
    void shouldParseSampleViolations(String filePath) throws IOException, URISyntaxException {
        // given
        String response = IOUtils.toString(Resources.getResource(filePath).toURI());

        // when
        List<Violation> violations = shellcheckResultParser.parse(response);

        // then
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("filenameOrJavaClassName").contains("myotherscript", "myscript");
        assertThat(violations)
                .extracting("message")
                .contains("[Code:1035] Message: You need a space after the [ and before the ].")
                .contains("[Code:2039] Message: In POSIX sh, echo flags are undefined.")
                .contains("[Code:2086] Message: Double quote to prevent globbing and word splitting.");
    }

    @Test
    public void shouldThrowExceptionWhenViolationWithUnknownMessageType() throws IOException, URISyntaxException {
        String response = IOUtils.toString(Resources.getResource("shellcheck/unknown-message-output.txt").toURI());

        Throwable thrown = catchThrowable(() -> shellcheckResultParser.parse(response));

        assertThat(thrown).isInstanceOf(ShellcheckException.class)
                .hasMessageStartingWith("Unknown message type returned by shellcheck (type = fatal)");
    }
}
