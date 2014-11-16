package pl.touk.sputnik.connector;

import lombok.Getter;

/**
 * Available connectors.
 */
public enum ConnectorType {
    GERRIT("gerrit"),
    STASH("stash");

    /** Name used in configuration file. */
    @Getter
    private final String name;

    ConnectorType(String name) {
        this.name = name;
    }
}
