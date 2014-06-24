package pl.touk.sputnik.connector.gerrit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.touk.sputnik.configuration.ConfigurationOption;

@AllArgsConstructor
@Getter
public enum GerritOption implements ConfigurationOption {

    HOST("gerrit.host", "Gerrit server host", "localhost"),
    PORT("gerrit.port", "Gerrit server port", "80"),
    USE_HTTPS("gerrit.useHttps", "Gerrit use https?", "false"),
    USERNAME("gerrit.username", "Gerrit server username", "user"),
    PASSWORD("gerrit.password", "Gerrit server password", "password");
    
    private String key;
    private String description;
    private String defaultValue;
}
