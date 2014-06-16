package pl.touk.sputnik.connector.stash;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.Map;

@Data
@Builder
public class SingleFileChanges {

    private String filename;
    private Map<Integer, ChangeType> changesMap;

    public void addChange(int line, ChangeType changeType) {
        getMap().put(line, changeType);
    }

    private Map<Integer, ChangeType> getMap() {
        if (changesMap == null) {
            changesMap = Maps.newHashMap();
        }
        return changesMap;
    }
}
