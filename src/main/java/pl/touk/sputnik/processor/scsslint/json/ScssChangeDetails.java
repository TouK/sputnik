package pl.touk.sputnik.processor.scsslint.json;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * scss-lint output with violations entry.
 * Used with JSON unmarshaller only.

    {
      "line": 1,
      "column": 13,
      "length": 7,
      "severity": "warning",
      "reason": "Color `#286BAF` should be written as `#286baf`",
      "linter": "HexNotation"
    },
    {
      "line": 2,
      "column": 14,
      "length": 7,
      "severity": "warning",
      "reason": "Color `#2173C9` should be written as `#2173c9`",
      "linter": "HexNotation"
    },
 */

@Data
@NoArgsConstructor
public final class ScssChangeDetails {
    private int line;
    private int column;
    private int length;
    private String severity;
    private String reason;
    private String linter;
}
