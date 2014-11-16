package pl.touk.sputnik.configuration;

import pl.touk.sputnik.connector.Connector;

/**
 * Used to inform that given {@link GeneralOption} is not supported by selected {#link {@link Connector}.
 */
public class GeneralOptionNotSupportedException extends RuntimeException {

    public GeneralOptionNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralOptionNotSupportedException(String message) {
        super(message);
    }
}
