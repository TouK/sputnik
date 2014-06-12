package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StashPatchset {
    public String pullRequestId;
    public String repositorySlug;
    public String projectKey;

}
