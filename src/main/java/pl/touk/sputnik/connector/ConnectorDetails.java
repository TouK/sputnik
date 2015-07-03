package pl.touk.sputnik.connector;

import lombok.Getter;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.apache.commons.lang3.Validate.notBlank;

@Getter
public class ConnectorDetails {

    private String host;
    private Integer port;
    private String path;
    private String username;
    private String password;
    private String useHttps;
    private boolean isHttps;

    public ConnectorDetails(Configuration configuration) {
        buildFromConfiguration(configuration);
        validate();
    }

    private void buildFromConfiguration(Configuration configuration) {
        host = configuration.getProperty(GeneralOption.HOST);
        port = Integer.valueOf(configuration.getProperty(GeneralOption.PORT));
        path = configuration.getProperty(GeneralOption.PATH);
        username = configuration.getProperty(GeneralOption.USERNAME);
        password = configuration.getProperty(GeneralOption.PASSWORD);
        useHttps = configuration.getProperty(GeneralOption.USE_HTTPS);
        isHttps = Boolean.parseBoolean(useHttps);
    }

    private void validate() {
        notBlank(host, "You must provide non blank connector host");
        notBlank(username, "You must provide non blank connector username");
        notBlank(password, "You must provide non blank connector password");
    }

}
