package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CrcMessage {
    private final int line;
    private final String message;

    public String getMessage() {
        return String.format("%s (%s)", message, line);
    }
}
