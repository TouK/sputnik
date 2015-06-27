package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import java.text.MessageFormat;

public class ReviewFormatter {

    private String commentFormat;
    private String problemFormat;

    /**
     * @param config a configuration instance
     */
    public ReviewFormatter(Configuration config) {
        this.commentFormat = config.getProperty(GeneralOption.MESSAGE_COMMENT_FORMAT);
        this.problemFormat = config.getProperty(GeneralOption.MESSAGE_PROBLEM_FORMAT);
    }

    public String stringify(@Nullable Object obj) {
        return obj == null ? "" : obj.toString();
    }
    /**
     * @param message a problem emitted by a processor
     * @param source the problem source
     * @return a formatted comment
     */
    public String formatProblem(String source, String message) {
        return MessageFormat.format(problemFormat, stringify(source), stringify(message));
    }

    /**
     * @param source a comment emitted by a processor
     * @param severity the problem severity
     * @param message the problem message
     * @return a formatted comment
     */
    public String formatComment(String source, @Nullable Severity severity, @NotNull String message) {
        return MessageFormat.format(commentFormat, stringify(source), stringify(severity), stringify(message));
    }
}
