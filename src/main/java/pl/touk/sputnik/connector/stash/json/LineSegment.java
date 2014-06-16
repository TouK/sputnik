package pl.touk.sputnik.connector.stash.json;

import java.util.List;

/*
"destination": 1,
            "source": 1,
            "line": "import sys",
            "truncated": false
 */
public class LineSegment {
    public int destination;
    public int source;
    public String line;
    public boolean truncated;
    public String conflictMarker;
    public List<Integer> commentIds;
}
