package pl.touk.sputnik.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Violation {
    private final String filenameOrJavaClassName;
    private final int line;
    private final String message;
    private final Severity severity;
}
