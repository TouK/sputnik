package pl.touk.sputnik.gerrit;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GerritPatchset {
    private final String changeId;
    private final String revisionId;
}
