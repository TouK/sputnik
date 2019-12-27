package pl.touk.sputnik.review.filter;

import com.google.common.collect.ImmutableList;

public class ESLintFilter extends FileExtensionFilter {

    public ESLintFilter() {
        super(ImmutableList.of("js", "jsx", "ts", "tsx"));
    }
}
