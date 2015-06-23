package pl.touk.sputnik.processor.sonar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.runner.api.EmbeddedRunner;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;

import com.google.common.collect.ImmutableList;

import freemarker.template.utility.StringUtil;


@RunWith(MockitoJUnitRunner.class)
public class SonarRunnerTest {
    private static final String PROPERTY_1 = "sonar-property1";
    private static final String PROPERTY_2 = "sonar-property2";

    private Configuration config;

    @Mock
    private EmbeddedRunner sonarRunner;

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
        SonarRunner runner = new SonarRunner(files, sonarRunner, config);
        runner.run();
        verify(sonarRunner).addProperties(any(Properties.class));
        verify(sonarRunner).execute();
    }

    @Test
    public void shouldLoadBaseProperties() throws IOException{
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("file");
        SonarRunner runner = new SonarRunner(files, sonarRunner, config);
        Properties properties = runner.loadBaseProperties();
        assertThat(properties.getProperty("sonar.foo")).isEqualTo("bar");
        assertThat(properties.getProperty("sonar.bar")).isEqualTo("bazz");
    }

    @Test
    public void shouldSetBaseSonarConfig() throws IOException{
        writeValidConfigFiles();
        List<String> files = ImmutableList.of("first", "second");
        SonarRunner runner = new SonarRunner(files, sonarRunner, config);
        Properties props = new Properties();
        runner.setAdditionalProperties(props);
        assertThat(props.getProperty(SonarProperties.INCLUDE_FILES)).contains("first");
        assertThat(props.getProperty(SonarProperties.INCLUDE_FILES)).contains("second");
        assertThat(StringUtil.split(props.getProperty(SonarProperties.INCLUDE_FILES), ',')).hasSize(2);
        assertThat(props.getProperty(SonarProperties.ANALISYS_MODE)).isEqualTo("incremental");
        assertThat(props.getProperty(SonarProperties.SCM_ENABLED)).isEqualTo("false");
        assertThat(props.getProperty(SonarProperties.SCM_STAT_ENABLED)).isEqualTo("false");
        assertThat(props.getProperty(SonarProperties.ISSUEASSIGN_PLUGIN)).isEqualTo("false");
        assertThat(props.getProperty(SonarProperties.EXPORT_PATH)).isEqualTo(SonarRunner.OUTPUT_FILE);
        assertThat(props.getProperty(SonarProperties.WORKDIR)).isEqualTo(SonarRunner.OUTPUT_DIR);
        assertThat(props.getProperty(SonarProperties.PROJECT_BASEDIR)).isEqualTo(".");
    }

    @Test(expected=IOException.class)
    public void shouldThrowWhenNoSonarFiles() throws IOException {
        List<String> files = ImmutableList.of("first", "second");
        SonarRunner runner = new SonarRunner(files, sonarRunner, config);
        runner.loadBaseProperties();
    }
}
