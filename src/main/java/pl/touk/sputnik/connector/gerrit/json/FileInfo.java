package pl.touk.sputnik.connector.gerrit.json;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gerrit FileInfo entry.
 * Used with JSON unmarshaller only.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private Status status = Status.MODIFIED;
    private boolean binary;
    private String old_path;
    private int lines_inserted;
    private int lines_deleted;

    @AllArgsConstructor
    public enum Status {
        MODIFIED("M"),
        ADDED("A"),
        DELETED("D"),
        RENAMED("R"),
        COPIED("C"),
        REWRITTEN("R");

        private final String symbol;

        @JsonValue
        public String getSymbol() {
            return symbol;
        }
    }



}
