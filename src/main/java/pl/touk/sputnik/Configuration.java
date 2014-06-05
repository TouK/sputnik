package pl.touk.sputnik;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.lang3.Validate.notBlank;

public class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private static final Configuration INSTANCE = new Configuration();
    private static final String SPUTNIK_PROPERTIES = "sputnik.properties";
    private static final String SPUTNIK_OPTS = "SPUTNIK_OPTS";
    private Properties properties = new Properties();

    @Getter @Setter private String configurationFilename;
    @Getter @Setter private String gerritChangeId;
    @Getter @Setter private String gerritRevisionId;
    @Getter @Setter private String connectorName;
    @Getter @Setter private String stashPullRequestId;

    private Configuration() {}

    @NotNull
    public static Configuration instance() {
        return INSTANCE;
    }

    @Nullable
    public String getProperty(@NotNull String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(key);
        }
        return value;
    }

    public void init() {
        notBlank(configurationFilename, "You need to provide filename with configuration properties");
        LOG.info("Initializing configuration properties from file {}", configurationFilename);

        properties = new Properties();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(configurationFilename);
            properties.load(fileReader);
        } catch (IOException e) {
            LOG.error("Configuration initialization failed");
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fileReader);
        }
    }
}
