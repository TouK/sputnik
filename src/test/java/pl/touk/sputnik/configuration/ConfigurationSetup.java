package pl.touk.sputnik.configuration;

import java.util.Map;
import java.util.Properties;

public class ConfigurationSetup {

    @SafeVarargs
    public final void setUp(final Map<String, String>... propertyMaps) {
        Properties properties = new Properties();
        for (Map<String, String> map: propertyMaps) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        Configuration.setInstance(configuration);
    }


}
