package pl.touk.sputnik.processor.sonar;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class SonarResultParserTest extends TestEnvironment {

    @Test
    void shouldParseSonarJsonMultiModuleFile() throws IOException {
        File resultFile = getResourceAsFile("json/sonar-result-mutli-module.json");

        SonarResultParser parser = new SonarResultParser(resultFile);
        ReviewResult reviewResult = parser.parseResults();

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(4);
        assertThat(reviewResult.getViolations())
                .extracting("filenameOrJavaClassName")
                .containsExactly(
                        "src/module1/dir/file.cs",
                        "src/module2/dir/file2.cs",
                        "src/module2/dir/file2.cs",
                        "src/module2/dir/file2.cs"
                );
        assertThat(reviewResult.getViolations())
        .extracting("severity")
        .containsExactly(
                Severity.WARNING,
                Severity.ERROR,
                Severity.ERROR,
                Severity.ERROR
        );
    }

    @Test
    void shouldParseSonarJsonSingleModuleFile() throws IOException {
        File resultFile = getResourceAsFile("json/sonar-result-single-module.json");

        SonarResultParser parser = new SonarResultParser(resultFile);
        ReviewResult reviewResult = parser.parseResults();

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).hasSize(4);
        assertThat(reviewResult.getViolations())
                .extracting("filenameOrJavaClassName")
                .containsExactly(
                        "src/dir/file.cs",
                        "src/dir/file2.cs",
                        "src/dir/file2.cs",
                        "src/dir/file2.cs"
                );
        assertThat(reviewResult.getViolations())
                .extracting("severity")
                .containsExactly(
                        Severity.WARNING,
                        Severity.ERROR,
                        Severity.ERROR,
                        Severity.ERROR
                );
    }

    @Test
    void shouldThrowAnExceptionWhenReportFileDoNotExists(){
        Throwable thrown = catchThrowable(() -> new SonarResultParser(new File("foo")).parseResults());

        assertThat(thrown).isInstanceOf(IOException.class);
    }

    @Test
    void shouldThrowWhenInvalidJsonReport() {
        Throwable thrown = catchThrowable(() -> new SonarResultParser(getResourceAsFile("json/invalid.json")).parseResults());

        assertThat(thrown).isInstanceOf(IOException.class);
    }

    @Test
    void testConvertSeverity() {
        assertThat(SonarResultParser.getSeverity("BLOCKER")).isEqualTo(Severity.ERROR);
        assertThat(SonarResultParser.getSeverity("CRITICAL")).isEqualTo(Severity.ERROR);
        assertThat(SonarResultParser.getSeverity("MAJOR")).isEqualTo(Severity.ERROR);
        assertThat(SonarResultParser.getSeverity("MINOR")).isEqualTo(Severity.WARNING);
        assertThat(SonarResultParser.getSeverity("INFO")).isEqualTo(Severity.INFO);
        assertThat(SonarResultParser.getSeverity("dummy")).isEqualTo(Severity.WARNING);
    }
}
