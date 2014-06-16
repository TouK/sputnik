package pl.touk.sputnik.connector.stash.json;


import java.util.List;

/*
{
    "type": "REMOVED",
    "lines": [
        {
            "destination": 1,
            "source": 1,
            "line": "import sys",
            "truncated": false
        }
    ],
    "truncated": false
},
 */
public class DiffSegment {

    public boolean truncated;
    public String type;
    public List<LineSegment> lines;
}
