package pl.touk.sputnik.connector;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;

public interface ConnectorValidator {
    /**
     * Validates if given options are supported by selected connector.
     *
     * @throws GeneralOptionNotSupportedException
     *             if passed configuration is not valid or not fully supported
    */
    void validate(Configuration configuration) throws GeneralOptionNotSupportedException;

}
