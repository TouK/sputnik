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

    public Set<String> getCommentsCrcSet() {
        if (commentsCrcSet == null) {
            commentsCrcSet = Sets.newHashSet();
        }
        return commentsCrcSet;
    }

    public void setComments(List<String> comments) {
        for (String comment : comments) {
            getCommentsCrcSet().add(comment);
        }
    }
}
