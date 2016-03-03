package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CliOption implements ConfigurationOption {
    CONF("cli.conf", "Configuration properties file", null),
    CHANGE_ID("cli.changeId", "Gerrit change id", null),
    REVISION_ID("cli.revisionId", "Gerrit revision id", null),
    PULL_REQUEST_ID("cli.pullRequestId", "Stash pull request id", null),
    API_KEY("cli.apiKey", "Optional API key for using Sputnik for Github", null);

    private String key;
    private String description;
    private String defaultValue;

    public String getCommandLineParam() {
        return key.substring(4);
    }
}
