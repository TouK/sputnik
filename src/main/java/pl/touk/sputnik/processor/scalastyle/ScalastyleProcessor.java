package pl.touk.sputnik.processor.scalastyle;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.scalastyle.*;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.*;
import scala.Option;
import scala.Some;

import java.io.File;
import java.util.List;

@Slf4j
public class ScalastyleProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "Scalastyle";
    private static final String SCALASTYLE_CONFIG = "scalastyle.config";

    private final MessageHelper messageHelper = new MessageHelper(ClassLoader.getSystemClassLoader());

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ReviewResult process(@NotNull Review review) {
        String scalastyleConfigFile = Configuration.instance().getProperty(SCALASTYLE_CONFIG);
        ScalastyleConfiguration configuration = ScalastyleConfiguration.readFromXml(scalastyleConfigFile);
        List<Message> messages = new ScalastyleChecker().checkFilesAsJava(configuration, toFileSpec(review.getIOFiles()));
        return toReviewResult(messages);
    }

    private List<FileSpec> toFileSpec(List<File> ioFiles) {
        List<FileSpec> fileSpecs = Lists.newArrayList();
        for (File file : ioFiles) {
            fileSpecs.add(new RealFileSpec(file.getAbsolutePath(), new Some<String>("UTF-8")));
        }
        return fileSpecs;
    }

    @SuppressWarnings("unchecked")
    private ReviewResult toReviewResult(List<Message> messages) {
        ReviewResult reviewResult = new ReviewResult();
        String currentFileName = null;
        for (Message msg : messages) {
            log.info("Got msg: {}", msg);

            if (msg instanceof StartFile) {
                StartFile startFile = (StartFile) msg;
                currentFileName = ((FileSpec)startFile.fileSpec()).name();
            }
            if (msg instanceof EndFile) {
                currentFileName = null;
            }

            if (msg instanceof StyleError) {
                StyleError styleError = (StyleError) msg;
                reviewResult.add(new Violation(currentFileName,
                        option(styleError.lineNumber(), Integer.valueOf(-1)),
                        messageHelper.message(styleError.clazz().getClassLoader(), styleError.key(), styleError.args()),
                        errorLevel(styleError.level())));
            }
        }
        return reviewResult;
    }

    @SuppressWarnings("unchecked")
    private <T> T option(Option option, T elseValue) {
        if (option.isDefined()) {
            return (T) option.get();
        } else {
            return elseValue;
        }
    }

    private Severity errorLevel(Level level) {
        if (level.name().equals(Level.Error())) {
            return Severity.ERROR;
        }
        if (level.name().equals(Level.Info())) {
            return Severity.INFO;
        }
        if (level.name().equals(Level.Warning())) {
            return Severity.WARNING;
        }

        log.warn("Got unrecognized severity level: {}", level);
        return Severity.IGNORE;
    }

    @NotNull
    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
