package pl.touk.sputnik.review.filter;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class PmdFilter extends FileExtensionFilter {
    private static final List<String> PMD_SUPPORTED_EXTENSIONS = ImmutableList.of(
            "java", ".class", "properties",
            "js",
            "xml",
            "xsl",
            "c" ,"h", "cc", "cpp", "cxx", "c++", "hh", "hpp", "hxx", "h++",
            "c", "h", "cpp", "hpp", "cxx", "hxx",
            "cs",
            "php", "phtml", "php3", "php4", "php5", "phps",
            "rb", "rbw",
            "f", "for", "f90", "f95"
            );


    public PmdFilter() {
        super(PMD_SUPPORTED_EXTENSIONS);
    }
}
