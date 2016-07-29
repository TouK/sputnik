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
    API_KEY("cli.apiKey", "Optional API key for using Sputnik as a service", null),
    BUILD_ID("cli.buildId", "Optional build id for using Sputnik as a service", null),
    PROVIDER("cli.provider", "Optional SCM provider (GitHub, GitLab) for using Sputnik as a service", null),
    FILE_REGEX("cli.fileRegex", "Review only file paths that fulfill provided regex", null),
    USERNAME("cli.username", "Passes username into connector", null),
    PASSWORD("cli.password", "Pass password into connector", null);

    private String key;
    private String description;
    private String defaultValue;

    public String getCommandLineParam() {
        return key.substring(4);
    }
}
