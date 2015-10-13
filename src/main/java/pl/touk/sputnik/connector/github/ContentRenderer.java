package pl.touk.sputnik.connector.github;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import pl.touk.sputnik.review.Review;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

class ContentRenderer {

    public static final String TEMPLATE_MUSTACHE = "issue.mustache";

    public String render(Review review) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(TEMPLATE_MUSTACHE);
        Writer sink = new StringWriter();
        mustache.execute(sink, review).flush();
        return sink.toString();
    }
}
