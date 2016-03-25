package pl.touk.sputnik.review;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewResult {

    private final List<Violation> violations = new ArrayList<Violation>();

    public void add(@NotNull Violation violation) {
        violations.add(violation);
    }
}
