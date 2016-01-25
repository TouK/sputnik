package pl.touk.sputnik.connector.saas.json;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FileViolation {
    private String file;
    private List<Violation> violations;
}
