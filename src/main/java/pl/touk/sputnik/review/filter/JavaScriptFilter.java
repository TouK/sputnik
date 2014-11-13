package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class JavaScriptFilter extends FileExtensionFilter {

    public JavaScriptFilter() {
        super(Collections.singletonList("js"));
    }
}
