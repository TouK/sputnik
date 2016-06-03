package pl.touk.sputnik.processor.eslint;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import pl.touk.sputnik.review.Violation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ESLintResultParserTest {

    private ESLintResultParser esLintResultParser = new ESLintResultParser();

    @Test
    public void shouldParseJsonResult() throws Exception {
        // given
        String response = IOUtils.toString(Resources.getResource("eslint/out.json").toURI());

        // when
        List<Violation> violations = esLintResultParser.parse(response);

        //then
        assertThat(violations.size()).isEqualTo(8);
        assertThat(violations).extracting("filenameOrJavaClassName").contains("examples/horrible.js");
        assertThat(violations).extracting("message")
                .contains("'unused' is defined but never used")
                .contains("Unexpected console statement.");
    }

}
