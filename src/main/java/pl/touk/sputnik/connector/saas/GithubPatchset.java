package pl.touk.sputnik.connector.saas;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubPatchset {
    private final String owner;
    private final String repository;
    private final Integer pullRequestId;
}
