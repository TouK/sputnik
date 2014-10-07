package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class JavaFilter extends FileExtensionFilter {

    public JavaFilter() {
        super(Collections.singletonList("java"));
    }
}
