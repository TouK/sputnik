package pl.touk.sputnik.processor.pylint.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PylintMessage {
    private String message;
    private String obj;
    private int column;
    private String path;
    private int line;
    private String type;
    private String symbol;
    private String module;
}
