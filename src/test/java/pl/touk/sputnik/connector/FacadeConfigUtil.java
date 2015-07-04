package pl.touk.sputnik.connector;

import java.util.HashMap;
import java.util.Map;

public final class FacadeConfigUtil {
    public static final Integer HTTP_PORT = 8089;
    public static final Integer HTTPS_PORT = 8443;
    public static final String PATH = "/review";

    public static Map<String, String> getHttpConfig(final String connectorType) {
        return new HashMap<String, String>() {{
            put("connector.type", connectorType);
            put("connector.host", "localhost");
            put("connector.path", PATH);
            put("connector.port", HTTP_PORT.toString());
            put("connector.username", "user");
            put("connector.password", "pass");
        }};
    }

    public static Map<String, String> getHttpsConfig(final String connectorType) {
        Map<String, String> httpConfig = getHttpConfig(connectorType);
        httpConfig.put("connector.port", HTTPS_PORT.toString());
        httpConfig.put("connector.useHttps", "true");
        return httpConfig;
    }


}
