package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class ShellFilter extends FileExtensionFilter {

    public ShellFilter() {
        super(Collections.singletonList("sh"));
    }
}
