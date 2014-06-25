package pl.touk.sputnik.connector.stash.json;

import lombok.Data;
import lombok.experimental.Builder;

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
    public int line;
    public String lineType = "CONTEXT";
    public String fileType = "TO";
    public String path;
    public String srcPath;
}
