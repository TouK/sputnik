package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class ScalaFilter extends FileExtensionFilter {

    public ScalaFilter() {
        super(Collections.singletonList("scala"));
    }
}
