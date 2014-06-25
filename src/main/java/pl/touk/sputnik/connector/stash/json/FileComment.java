package pl.touk.sputnik.connector.stash.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 {
         "text": "A pithy comment on a particular line within a file.",
         "anchor": {
             "line": 1,
             "lineType": "CONTEXT",
             "fileType": "FROM"
             "path": "path/to/file",
             "srcPath": "path/to/file"
         }
     }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileComment {
    private String text;
    private Anchor anchor;
}
