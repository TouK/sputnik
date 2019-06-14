package pl.touk.sputnik.connector.stash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SingleFileChanges {

    private final String filename;
    @Getter(AccessLevel.NONE)
    private final Map<Integer, ChangeDetails> changesMap = new HashMap<>();

    public void addChange(int line, ChangeType changeType, List<String> comments) {
        changesMap.put(line, new ChangeDetails(changeType, new ArrayList<>(comments)));
    }

    public ChangeType getChangeType(int line) {
        return changesMap.get(line).changeType;
    }

    public boolean containsComment(Integer line, String message) {
        return changesMap.containsKey(line) && changesMap.get(line).comments.contains(message);
    }

    public boolean containsChange(Integer line) {
        return changesMap.containsKey(line);
    }

    @AllArgsConstructor
    private static class ChangeDetails {
        private final ChangeType changeType;
        private final List<String> comments;
    }

}
