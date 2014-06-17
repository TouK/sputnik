package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.touk.sputnik.configuration.ConfigurationOption;

@AllArgsConstructor
@Getter
public enum StashOption implements ConfigurationOption {

    HOST("stash.host", "Shash server host"),
    PORT("stash.port", "Stash server port"),
    USE_HTTPS("stash.useHttps", "Use https?"),
    USERNAME("stash.username", "Stash server username"),
    PASSWORD("stash.password", "Stash server password"),
    PROJECT_KEY("stash.projectKey", "Stash server projectKey"),
    REPOSITORY_SLUG("stash.repositorySlug", "Stash server repositorySlug");

    private String key;
    private String description;
}
