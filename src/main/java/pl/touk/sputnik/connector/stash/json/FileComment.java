package pl.touk.sputnik.connector.stash.json;

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
public class FileComment {
    public String text;
    public Anchor anchor;
}
