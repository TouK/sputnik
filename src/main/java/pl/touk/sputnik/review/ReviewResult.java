package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReviewResult {
    private List<Violation> violations = new ArrayList<Violation>();

    public void add(@NotNull Violation violation) {
        violations.add(violation);
    }


}
