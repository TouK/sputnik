package pl.touk.sputnik.processor.sonar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.sonar.runner.api.EmbeddedRunner;
import com.google.common.annotations.VisibleForTesting;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;

@Slf4j
@AllArgsConstructor
public class SonarRunner {

    private final List<String> files;
    private final EmbeddedRunner sonarEnbeddedRunner;

    @VisibleForTesting
    static final String OUTPUT_DIR = ".sonar";

    @VisibleForTesting
    static final String OUTPUT_FILE = "sonar-report.json";

    /**
     * Load base sonar configuration files specified by GeneralOption.SONAR_PROPERTIES
     * configuration key
     * @return a Properties instance
     * @throws IOException
     */
    @VisibleForTesting
    Properties loadBaseProperties() throws IOException {
        final Properties props = new Properties();
        for (final String property: StringUtils.split(ConfigurationHolder.instance().getProperty(GeneralOption.SONAR_PROPERTIES), ',')){
            final File propertyFile = new File(StringUtils.strip(property));
            log.info("Loading {}", propertyFile.getAbsolutePath());
            props.load(new FileInputStream(propertyFile));
        }
        return props;
    }

    /**
     * Runs Sonar.
     *
     * @throws IOException
     * @return the json file containing the results.
     */
    public File run() throws IOException {
        Properties props = loadBaseProperties();
        setAdditionalProperties(props);
        log.info("Sonar configuration: {}", props.toString());
        sonarEnbeddedRunner.addProperties(props);
        sonarEnbeddedRunner.execute();
        return new File(OUTPUT_DIR, OUTPUT_FILE);
    }

    /**
     * Set additional properties needed for sonar to run
     * @param props a Properties instance
     */
    @VisibleForTesting
    void setAdditionalProperties(Properties props) {
        props.put(SonarProperties.INCLUDE_FILES, StringUtils.join(files, ", "));
        props.put(SonarProperties.ANALISYS_MODE, "incremental");
        props.put(SonarProperties.SCM_ENABLED, "false");
        props.put(SonarProperties.SCM_STAT_ENABLED, "false");
        props.put(SonarProperties.ISSUEASSIGN_PLUGIN, "false");
        props.put(SonarProperties.EXPORT_PATH, OUTPUT_FILE);
        props.put(SonarProperties.VERBOSE, ConfigurationHolder.instance().getProperty(GeneralOption.SONAR_VERBOSE));
        props.put(SonarProperties.WORKDIR, OUTPUT_DIR);
        props.put(SonarProperties.PROJECT_BASEDIR, ".");
    }
}
