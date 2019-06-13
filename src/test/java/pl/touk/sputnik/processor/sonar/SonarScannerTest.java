package pl.touk.sputnik.processor.sonar;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sonarsource.scanner.api.EmbeddedScanner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SonarScannerTest {
    private static final String PROPERTY_1 = "sonar-property1";
    private static final String PROPERTY_2 = "sonar-property2";

    private Configuration config;

    @Mock
    private EmbeddedScanner sonarEmbeddedScanner;

    @BeforeEach
    void setUp() {
        config = ConfigurationBuilder.initFromResource("test-sonar.properties");
    }

    @AfterEach
    void tearDown() {
        new File(PROPERTY_1).delete();
        new File(PROPERTY_2).delete();
    }

    private void writeValidConfigFiles() throws IOException {
        FileUtils.writeStringToFile(new File(PROPERTY_1), "sonar.foo=bar");
        FileUtils.writeStringToFile(new File(PROPERTY_2), "sonar.bar=bazz");
    }

    @Test
    void shouldRun() throws IOException {
        //given
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("file");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);

        //when
        runner.run();

        //then
        verify(sonarEmbeddedScanner, times(1)).addGlobalProperties(anyMapOf(String.class, String.class));
        verify(sonarEmbeddedScanner).start();
        verify(sonarEmbeddedScanner).execute(anyMapOf(String.class, String.class));
    }

    @Test
    void shouldLoadBaseProperties() throws IOException {
        //given
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("file");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);

        //when
        Map<String, String> properties = runner.loadBaseProperties();

        //then
        assertThat(properties.get("sonar.foo")).isEqualTo("bar");
        assertThat(properties.get("sonar.bar")).isEqualTo("bazz");
    }

    @Test
    void shouldSetBaseSonarConfig() throws IOException {
        //given
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("first", "second");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);
        Map<String, String> props = new HashMap<>();

        //when
        runner.setAdditionalProperties(props);

        //then
        assertThat(props.get(SonarProperties.INCLUDE_FILES)).contains("first");
        assertThat(props.get(SonarProperties.INCLUDE_FILES)).contains("second");
        assertThat(StringUtils.split(props.get(SonarProperties.INCLUDE_FILES), ',')).hasSize(2);
        assertThat(props.get(SonarProperties.SCM_ENABLED)).isEqualTo("false");
        assertThat(props.get(SonarProperties.SCM_STAT_ENABLED)).isEqualTo("false");
        assertThat(props.get(SonarProperties.ISSUEASSIGN_PLUGIN)).isEqualTo("false");
        assertThat(props.get(SonarProperties.EXPORT_PATH)).isEqualTo(SonarScanner.OUTPUT_FILE);
        assertThat(props.get(SonarProperties.WORKDIR)).isEqualTo(SonarScanner.OUTPUT_DIR);
        assertThat(props.get(SonarProperties.PROJECT_BASEDIR)).isEqualTo(".");
        assertThat(props.get(SonarProperties.SOURCES)).isEqualTo(".");
    }

    @Test
    void shouldThrowWhenNoSonarFiles() {
        //given
        List<String> files = ImmutableList.of("first", "second");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);

        //when
        Throwable thrown = catchThrowable(runner::loadBaseProperties);

        //then
        assertThat(thrown).isInstanceOf(IOException.class);
    }
}
