package pl.touk.sputnik.processor.tools.externalprocess;

import pl.touk.sputnik.review.Violation;

import java.util.List;

public interface ExternalProcessResultParser {
    List<Violation> parse(String output);
}
