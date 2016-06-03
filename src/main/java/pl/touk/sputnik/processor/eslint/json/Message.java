package pl.touk.sputnik.processor.eslint.json;

import lombok.Data;

import java.util.Map;

@Data
public class Message {

    private String ruleId;
    private Integer severity;
    private String message;
    private Integer line;
    private Integer column;
    private String nodeType;
    private String source;
    private Map<String, Object> fix;

}
