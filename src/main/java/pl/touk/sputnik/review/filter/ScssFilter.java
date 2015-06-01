package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class ScssFilter extends FileExtensionFilter {

    public ScssFilter() {
        super(Collections.singletonList("scss"));
    }
}
