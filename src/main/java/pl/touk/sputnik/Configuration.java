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
    private static final String REVIEW_PROPERTIES = "sputnik.properties";
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
        synchronized (this) {
            properties = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(REVIEW_PROPERTIES);
                properties.load(inputStream);
            } catch (IOException e) {
                LOG.error("Configuration initialization failed", e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

}
