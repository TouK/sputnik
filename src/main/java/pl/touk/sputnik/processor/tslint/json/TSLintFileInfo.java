package pl.touk.sputnik.processor.tslint.json;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TSLint output with violations entry.
 * Used with JSON unmarshaller only.

[
    {
        "name":"service.ts",
        "failure":"exceeds maximum line length of 120",
        "startPosition":
        {
            "position":897,
            "line":18,
            "character":0
        },
        "endPosition":
        {
            "position":1028,
            "line":18,
            "character":131
        },
        "ruleName":"max-line-length"
    },
    {
        "name":"administration.ts",
        "failure":"exceeds maximum line length of 120",
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
        "ruleName":"max-line-length"
    }
]

 */
@Data
@NoArgsConstructor
public final class TSLintFileInfo {
    private String name;
    private String failure;

    private ChangePosition startPosition;

    private ChangePosition endPosition;

    private String ruleName;
}
