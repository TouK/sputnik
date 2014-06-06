package pl.touk.sputnik.connector.stash;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.touk.sputnik.Patchset;

@Data
@AllArgsConstructor
public class StashPatchset implements Patchset {
    public String pullRequestId;
    public String repositorySlug;
    public String projectKey;

}
