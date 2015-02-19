package pl.touk.sputnik.connector.github;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class DiffParserTest {

    @Test
    public void shouldParseGitDiff() throws Exception {
        // given
        // file to parse
        String input = Resources.toString(Resources.getResource("github.diff"), Charsets.UTF_8);

        // when
        // parse file
        Map<String, Map<Integer, Integer>> parsedDiff = new DiffParser().parse(input);

        // then
        // have a map of - file, list of tuples (line changed, commit id)
        assertThat(parsedDiff.get("build.gradle")).hasSize(37);
        assertThat(parsedDiff.get("build.gradle"))
                .contains(entry(69, 22),
                        entry(70, 23),
                        entry(7, 4),
                        entry(71, 24),
                        entry(8, 5),
                        entry(72, 25));
    }
}
