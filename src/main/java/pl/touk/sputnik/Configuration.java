package pl.touk.sputnik;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
    private static final Configuration INSTANCE = new Configuration();
    private static final String SPUTNIK_PROPERTIES = "sputnik.properties";
    private static final String SPUTNIK_OPTS = "SPUTNIK_OPTS";
    private Properties properties = new Properties();

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
        LOG.info("Initializing configuration properties");
        String propertiesFilename = getPropertiesFilename();
        if (propertiesFilename == null) {
            return;
        }

        properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SPUTNIK_PROPERTIES);
            properties.load(inputStream);
        } catch (IOException e) {
            LOG.error("Configuration initialization failed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Nullable
    private String getPropertiesFilename() {
        String filename = System.getProperty(SPUTNIK_PROPERTIES);
        if (StringUtils.isBlank(filename)) {
            LOG.warn("Didn't read any properties file - system property {} is missing.", SPUTNIK_PROPERTIES);
            LOG.warn("If you want to provide proprties file in future you can to this with specyfing -D{}=\"/home/user/review.pproperties\" option.", SPUTNIK_PROPERTIES);
            LOG.warn("You can also set up {} variable, so they will be passed to JVM.", SPUTNIK_OPTS);
        } else {
            LOG.info("Using properties file {}", filename);
        }
        return filename;
    }

}
