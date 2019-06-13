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
    public int line;
    public String lineType;
    public String fileType;
    public String path;
    public String srcPath;
}
