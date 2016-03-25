package pl.touk.sputnik.review;

import lombok.Data;

@Data
public class Comment {
    private final int line;
    private final String message;
}
