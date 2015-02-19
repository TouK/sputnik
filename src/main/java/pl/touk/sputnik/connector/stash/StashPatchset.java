package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StashPatchset {
    public final String pullRequestId;
    public final String repositorySlug;
    public final String projectKey;
}
