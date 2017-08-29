package pl.touk.sputnik.processor.scalastyle;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.scalastyle.EndFile;
import org.scalastyle.FileSpec;
import org.scalastyle.Level;
import org.scalastyle.Message;
import org.scalastyle.MessageHelper;
import org.scalastyle.RealFileSpec;
import org.scalastyle.ScalastyleChecker;
import org.scalastyle.ScalastyleConfiguration;
import org.scalastyle.StartFile;
import org.scalastyle.StyleError;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.ScalaFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;
import scala.Option;
import scala.Some;

import java.io.File;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class ScalastyleProcessor implements ReviewProcessor {
    private static final String SOURCE_NAME = "Scalastyle";

    private final MessageHelper messageHelper = new MessageHelper(ClassLoader.getSystemClassLoader());

    @NotNull
    private final Configuration config;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ReviewResult process(@NotNull Review review) {
        String scalastyleConfigFile = config.getProperty(GeneralOption.SCALASTYLE_CONFIGURATION_FILE);
        ScalastyleConfiguration configuration = ScalastyleConfiguration.readFromXml(scalastyleConfigFile);
        List<Message> messages = new ScalastyleChecker().checkFilesAsJava(configuration, toFileSpec(review.getFiles(new ScalaFilter(), new IOFileTransformer())));
        return toReviewResult(messages);
    }

    private List<FileSpec> toFileSpec(List<File> ioFiles) {
        List<FileSpec> fileSpecs = Lists.newArrayList();
        for (File file : ioFiles) {
            fileSpecs.add(new RealFileSpec(file.getAbsolutePath(), new Some<>("UTF-8")));
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
                currentFileName = startFile.fileSpec().name();
            }
            if (msg instanceof EndFile) {
                currentFileName = null;
            }

            if (msg instanceof StyleError) {
                StyleError styleError = (StyleError) msg;
                reviewResult.add(new Violation(currentFileName,
                        option(styleError.lineNumber(), 1),
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
