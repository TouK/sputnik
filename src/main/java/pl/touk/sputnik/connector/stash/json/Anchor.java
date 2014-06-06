package pl.touk.sputnik.connector.stash.json;

/*
{
             "line": 1,
             "lineType": "CONTEXT",
             "fileType": "FROM"
             "path": "path/to/file",
             "srcPath": "path/to/file"
         }
 */
public class Anchor {
    public int line;
    public String lineType = "CONTEXT";
    public String fileType = "FROM";
    public String path;
    public String srcPath;
}
