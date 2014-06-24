package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.touk.sputnik.configuration.ConfigurationOption;

@AllArgsConstructor
@Getter
public enum StashOption implements ConfigurationOption {

    HOST("stash.host", "Stash server host", "localhost"),
    PORT("stash.port", "Stash server port", "80"),
    USE_HTTPS("stash.useHttps", "Stash use https?", "false"),
    USERNAME("stash.username", "Stash server username", "user"),
    PASSWORD("stash.password", "Stash server password", "password"),
    PROJECT_KEY("stash.projectKey", "Stash server projectKey", null),
    REPOSITORY_SLUG("stash.repositorySlug", "Stash server repositorySlug", null);

    private String key;
    private String description;
    private String defaultValue;
}
