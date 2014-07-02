package pl.touk.sputnik.connector.gerrit.json;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.touk.sputnik.review.ModificationType;

/**
 * Gerrit FileInfo entry.
 * Used with JSON unmarshaller only.
 */
@Data
@NoArgsConstructor
public class FileInfo {
    private Status status = Status.MODIFIED;
    private boolean binary;
    private String old_path;
    private int lines_inserted;
    private int lines_deleted;

    @AllArgsConstructor
    public enum Status {
        MODIFIED(ModificationType.MODIFIED, "M"),
        ADDED(ModificationType.ADDED, "A"),
        DELETED(ModificationType.DELETED, "D"),
        RENAMED(ModificationType.RENAMED, "R"),
        COPIED(ModificationType.COPIED, "C"),
        REWRITTEN(ModificationType.REWRITTEN, "R");

        @Getter
        private final ModificationType modificationType;
        private final String symbol;

        @JsonValue
        public String getSymbol() {
            return symbol;
        }
    }



}
