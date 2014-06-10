package pl.touk.sputnik.cli;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CliOption {
    CHANGE_ID("cli.changeId"),
    REVISION_ID("cli.revisionId"),
    PULL_REQUEST_ID("cli.pullRequestId");

    private String name;
}
