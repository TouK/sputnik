package pl.touk.sputnik.processor.shellcheck.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShellcheckMessage {
    private String file;
    private int line;
    private Integer endLine;
    private int column;
    private Integer endColumn;
    private String level;
    private String code;
    private String message;
    private String fix;
}
