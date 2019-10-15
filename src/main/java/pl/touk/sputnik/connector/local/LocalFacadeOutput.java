package pl.touk.sputnik.connector.local;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class LocalFacadeOutput {
    void info(String message, Object ... arguments) {
        log.info(message, arguments);
    }

    void warn(String message, Object ... arguments) {
        log.warn(message, arguments);
    }
}
