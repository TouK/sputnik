package pl.touk.sputnik.processor.pylint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.exec.ExternalProcess;

import javax.annotation.Nullable;
import java.util.List;

@Slf4j
public class PylintExecutor {
    private static final String pylintExecutable = "pylint";
    private static final String pylintOutputFormat = "--output-format=json";
    private static final String pylintRcfileName = "--rcfile=";

    private String rcfileName;

    public PylintExecutor(@Nullable String rcfileName) {
        this.rcfileName = rcfileName;
    }

    public String runOnFile(String filePath) {
        log.info("Review on file: " + filePath);
        return new ExternalProcess().executeCommand(buildParams(filePath));
    }

    @NotNull
    private String[] buildParams(String filePath) {
        List<String> basicPylintArgs = ImmutableList.of(
                pylintExecutable,
                pylintOutputFormat);
        List<String> rcfileNameArg = getRcfileNameAsList();
        List<String> filePathArg = ImmutableList.of(filePath);
        List<String> allArgs = Lists.newArrayList(Iterables.concat(basicPylintArgs, rcfileNameArg, filePathArg));
        return allArgs.toArray(new String[allArgs.size()]);
    }

    private List<String> getRcfileNameAsList() {
        if (rcfileName != null) {
            return ImmutableList.of(pylintRcfileName + rcfileName);
        } else {
            return ImmutableList.of();
        }
    }
}
