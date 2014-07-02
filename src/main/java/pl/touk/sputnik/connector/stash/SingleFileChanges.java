package pl.touk.sputnik.connector.stash;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class SingleFileChanges {

    private String filename;
    private Map<Integer, ChangeType> changesMap;
    private Set<String> commentsCrcSet;

    public void addChange(int line, ChangeType changeType) {
        getMap().put(line, changeType);
    }

    private Map<Integer, ChangeType> getMap() {
        if (changesMap == null) {
            changesMap = Maps.newHashMap();
        }
        return changesMap;
    }

    private Set<String> getSet() {
        if (commentsCrcSet == null) {
            commentsCrcSet = Sets.newHashSet();
        }
        return commentsCrcSet;
    }

    public void setComments(List<String> comments) {
        for (String comment : comments) {
            getSet().add(extractCrc(comment));
        }
    }

    private String extractCrc(String comment) {
        String[] commentLines = comment.split("\n");
        String lastLine = commentLines[commentLines.length - 1];

        if (lastLine.startsWith("crc:")) {
            return lastLine.replace("crc:", "");
        }
        return "";
    }
}
