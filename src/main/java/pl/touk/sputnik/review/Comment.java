package pl.touk.sputnik.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
    private final int line;
    private final String message;
}
