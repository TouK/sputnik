package pl.touk.sputnik.review;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReviewResult {
    @Getter
    private final List<Violation> violations = new ArrayList<Violation>();

    public void add(@NotNull Violation violation) {
        violations.add(violation);
    }
}
