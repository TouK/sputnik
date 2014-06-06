package pl.touk.sputnik.gerrit;


import lombok.AllArgsConstructor;
import lombok.Data;
import pl.touk.sputnik.Patchset;

@Data
@AllArgsConstructor
public class GerritPatchset implements Patchset {
    private final String changeId;
    private final String revisionId;
}
