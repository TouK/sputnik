package pl.touk.sputnik.connector.github;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubPatchset {
    private final Integer pullRequestId;
    private final String projectPath;
}
