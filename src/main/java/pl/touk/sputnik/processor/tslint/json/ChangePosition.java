package pl.touk.sputnik.processor.tslint.json;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TSLint output with violations entry.
 * Used with JSON unmarshaller only.
 * 
    "startPosition":
    {
            "position":1144,
            "line":20,
            "character":0
    },
    "endPosition":
    {
            "position":1275,
            "line":20,
            "character":131
    },
 */
@Data
@NoArgsConstructor
public final class ChangePosition {
    private int position;
    private int line;
    private int character;
}
