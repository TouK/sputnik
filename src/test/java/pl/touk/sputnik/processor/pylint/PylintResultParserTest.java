package pl.touk.sputnik.processor.pylint;

import com.google.common.io.Resources;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class PylintResultParserTest {

    private PylintResultParser pylintResultParser;

    @Before
    public void setUp() {
        pylintResultParser = new PylintResultParser();
    }

    @Test
    @Parameters({
            "pylint/sample-output.txt",
            "pylint/sample-output-no-header.txt"
    })
    public void shouldParseSampleViolations(String filePath) throws IOException, URISyntaxException {
        // given
        String response = IOUtils.toString(Resources.getResource(filePath).toURI());

        // when
        List<Violation> violations = pylintResultParser.parse(response);

        // then
        assertThat(violations).hasSize(8);
        assertThat(violations).extracting("filenameOrJavaClassName").contains("PythonTest.py");
        assertThat(violations)
                .extracting("message")
                .contains("Invalid argument name \"n\" [invalid-name]")
                .contains("Black listed name \"bar\" [blacklisted-name]")
                .contains("Black listed name \"foo\" [blacklisted-name]");
    }
}
