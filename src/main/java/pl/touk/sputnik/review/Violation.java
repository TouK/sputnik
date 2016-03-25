package pl.touk.sputnik.review;

import lombok.*;

@Data
public class Violation {
    private final String filenameOrJavaClassName;
    private final int line;
    private final String message;
    private final Severity severity;
}
