package pl.touk.sputnik.review.filter;

import java.util.Collections;

public class KotlinFilter extends FileExtensionFilter {

    public KotlinFilter() {
        super(Collections.singletonList("kt"));
    }
}
