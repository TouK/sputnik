package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class PythonFilter extends FileExtensionFilter {

    public PythonFilter() {
        super(Collections.singletonList("py"));
    }
}
