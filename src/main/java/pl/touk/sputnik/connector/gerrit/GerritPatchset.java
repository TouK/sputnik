package pl.touk.sputnik.connector.gerrit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GerritPatchset {
    private final String changeId;
    private final String revisionId;
    private final String tag;
}
