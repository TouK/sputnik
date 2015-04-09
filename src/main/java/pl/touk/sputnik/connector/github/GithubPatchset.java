package pl.touk.sputnik.connector.github;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubPatchset {
    public final Integer pullRequestId;
    public final String projectPath;
}
