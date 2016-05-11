package pl.touk.sputnik.connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.touk.sputnik.configuration.Provider;

@Data
@AllArgsConstructor
public class Patchset {
    private final Integer pullRequestId;
    private final String projectPath;
    private final Provider provider;
}
