package pl.touk.sputnik.processor.sonar;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonarsource.scanner.api.EmbeddedScanner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SonarScannerTest {
    private static final String PROPERTY_1 = "sonar-property1";
    private static final String PROPERTY_2 = "sonar-property2";

    private Configuration config;

    @Mock
    private EmbeddedScanner sonarEmbeddedScanner;

    @Before
    public void setUp() throws FileNotFoundException {
        config = ConfigurationBuilder.initFromResource("test-sonar.properties");
    }

    @After
    public void tearDown() {
        new File(PROPERTY_1).delete();
        new File(PROPERTY_2).delete();
    }

    private void writeValidConfigFiles() throws IOException {
        FileUtils.writeStringToFile(new File(PROPERTY_1), "sonar.foo=bar");
        FileUtils.writeStringToFile(new File(PROPERTY_2), "sonar.bar=bazz");
    }

    @Test
    public void shouldRun() throws IOException {
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("file");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);
        runner.run();
        verify(sonarEmbeddedScanner, times(1)).addGlobalProperties(anyMapOf(String.class, String.class));
        verify(sonarEmbeddedScanner).start();
        verify(sonarEmbeddedScanner).execute(anyMapOf(String.class, String.class));
    }

    @Test
    public void shouldLoadBaseProperties() throws IOException{
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("file");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);
        Map<String, String> properties = runner.loadBaseProperties();
        assertThat(properties.get("sonar.foo")).isEqualTo("bar");
        assertThat(properties.get("sonar.bar")).isEqualTo("bazz");
    }

    @Test
    public void shouldSetBaseSonarConfig() throws IOException{
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("first", "second");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);
        Map<String, String> props = new HashMap<>();
        runner.setAdditionalProperties(props);
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

    @Test(expected=IOException.class)
    public void shouldThrowWhenNoSonarFiles() throws IOException {
        List<String> files = ImmutableList.of("first", "second");
        SonarScanner runner = new SonarScanner(files, sonarEmbeddedScanner, config);
        runner.loadBaseProperties();
    }
}
