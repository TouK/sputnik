package pl.touk.sputnik.connector.stash.json;

import lombok.Builder;
import lombok.Data;

/*
{
             "line": 1,
             "lineType": "CONTEXT",
             "fileType": "FROM"
             "path": "path/to/file",
             "srcPath": "path/to/file"
         }
 */
@Data
@Builder
public class Anchor {
    private int line;
    private String lineType;
    private String fileType;
    private String path;
    private String srcPath;
}
