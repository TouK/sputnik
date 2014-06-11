package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CliOption {
    CHANGE_ID("cli.changeId"),
    REVISION_ID("cli.revisionId"),
    PULL_REQUEST_ID("cli.pullRequestId");

    private String key;
}
