package pl.touk.sputnik.configuration;

import com.google.common.io.Resources;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static Configuration INSTANCE = new Configuration();
    private static final String CLI_OPTION_PREFIX = "cli.";
    private Properties properties = new Properties();

    @Getter @Setter private String configurationFilename;
    @Getter @Setter private String configurationResource;

    Configuration() {}

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

    @Nullable
    public String getProperty(CliOption cliOption) {
        return getProperty(cliOption.getKey());
    }

    public void init() {
        if (StringUtils.isEmpty(configurationFilename) && StringUtils.isEmpty(configurationResource)) {
            throw new IllegalArgumentException("You need to provide filename or resource with configuration properties");
        }

        InputStream propertiesStream = null;
        properties = new Properties();
        try {
            if (StringUtils.isNotEmpty(configurationFilename)) {
                propertiesStream = initFileStream(configurationFilename);
            } else if (StringUtils.isNotEmpty(configurationResource)) {
                propertiesStream = initResourceStream(configurationResource);
            }
            properties.load(propertiesStream);
        } catch (IOException e) {
            LOG.error("Configuration initialization failed");
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(propertiesStream);
        }
    }

    public void reset() {
        properties = null;
        configurationFilename = null;
        configurationResource = null;
    }

    public void updateWithCliOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            properties.setProperty(CLI_OPTION_PREFIX + option.getArgName(), option.getValue());
        }
    }

    static void setInstance(Configuration instance) {
        INSTANCE = instance;
    }

    void setProperties(Properties properties) {
        this.properties = properties;
    }

    private InputStream initResourceStream(String resource) throws IOException {
        LOG.info("Initializing configuration properties from resource {}", resource);
        return Resources.newInputStreamSupplier(Resources.getResource(resource)).getInput();
    }

    private InputStream initFileStream(String filename) throws FileNotFoundException {
        LOG.info("Initializing configuration properties from file {}", filename);
        return new FileInputStream(filename);
    }
}
