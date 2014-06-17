package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CliOption implements ConfigurationOption {
    CONF("cli.conf", "Configuration properties file"),
    CHANGE_ID("cli.changeId", "GIT change id"),
    REVISION_ID("cli.revisionId", "Revision id"),
    PULL_REQUEST_ID("cli.pullRequestId", "Pull request id"),
    CONNECTOR("cli.connector", "Connector: <stash|gerrit>");

    private String key;
    private String description;
    
    public String getCommandLineParam() {
        return key.substring(4);
    }
}
