package pl.touk.sputnik.connector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supported connectors.
 */
@Slf4j
public enum ConnectorType {
    GERRIT("gerrit"),
    STASH("stash"),
    GITHUB("github"),
    SAAS("saas"),
    LOCAL("local");

    /** Name used in configuration file. */
    @Getter
    private final String name;

    ConnectorType(String name) {
        this.name = name;
    }

    @NotNull
    public static ConnectorType getValidConnectorType(@Nullable String connectorName) {
        for (ConnectorType connectorType : values()) {
            if (connectorType.getName().equals(connectorName)) {
                return connectorType;
            }
        }

        log.error(String.format("Given connector (%s) is not valid, returned default one!", connectorName));
        return GERRIT;
    }
}
