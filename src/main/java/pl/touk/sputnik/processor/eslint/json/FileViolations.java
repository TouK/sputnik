package pl.touk.sputnik.processor.eslint.json;

import lombok.Data;

import java.util.List;

@Data
public class FileViolations {

    private String filePath;
    private List<Message> messages;
    private Integer errorCount;
    private Integer warningCount;

}
