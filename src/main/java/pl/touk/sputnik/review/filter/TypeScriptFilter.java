package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class TypeScriptFilter extends FileExtensionFilter {

    public TypeScriptFilter() {
        super(Collections.singletonList("ts"));
    }
}
