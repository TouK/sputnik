package pl.touk.sputnik;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private static final String SPUTNIK_PROPERTIES = "sputnik.properties";
    private static final String SPUTNIK_OPTS = "SPUTNIK_OPTS";
    private static final String CLI_OPTION_PREFIX = "cli.";
    private Properties properties = new Properties();

    @Getter @Setter private String configurationFilename;
    @Getter @Setter private String gerritChangeId;
    @Getter @Setter private String gerritRevisionId;
    @Getter @Setter private String connectorName;
    @Getter @Setter private String stashPullRequestId;

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

    public void init() {
        notBlank(configurationFilename, "You need to provide filename with configuration properties");
        log.info("Initializing configuration properties from file {}", configurationFilename);

        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationFilename);
            if (inputStream == null) {
                throw new RuntimeException("Configuration file " + configurationFilename + " cannot be loaded");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void updateWithCliOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            properties.setProperty(CLI_OPTION_PREFIX + option.getArgName(), option.getValue());
        }
    }
}
