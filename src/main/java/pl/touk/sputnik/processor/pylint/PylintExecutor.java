package pl.touk.sputnik.processor.pylint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.exec.ExternalProcess;

import java.util.List;

@Slf4j
class PylintExecutor {
    private static final String PYLINT_EXECUTABLE = "pylint";
    private static final String PYLINT_OUTPUT_FORMAT = "--output-format=json";
    private static final String PYLINT_RCFILE_NAME = "--rcfile=";

    private String rcfileName;

    PylintExecutor(@Nullable String rcfileName) {
        this.rcfileName = rcfileName;
    }

    String runOnFile(String filePath) {
        log.info("Review on file: " + filePath);
        return new ExternalProcess().executeCommand(buildParams(filePath));
    }

    @NotNull
    private String[] buildParams(String filePath) {
        List<String> basicPylintArgs = ImmutableList.of(
                PYLINT_EXECUTABLE,
                PYLINT_OUTPUT_FORMAT);
        List<String> rcfileNameArg = getRcfileNameAsList();
        List<String> filePathArg = ImmutableList.of(filePath);
        List<String> allArgs = Lists.newArrayList(Iterables.concat(basicPylintArgs, rcfileNameArg, filePathArg));
        return allArgs.toArray(new String[allArgs.size()]);
    }

    private List<String> getRcfileNameAsList() {
        if (rcfileName == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of(PYLINT_RCFILE_NAME + rcfileName);
    }
}
