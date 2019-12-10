package pl.touk.sputnik.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Comment {
    private final int line;
    private final String message;
    private final Severity severity;
}
