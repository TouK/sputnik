package pl.touk.sputnik.processor.shellcheck;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.exec.ExternalProcess;

import java.util.List;

@Slf4j
class ShellcheckExecutor {
    private static final String SHELLCHECK_EXECUTABLE = "shellcheck";
    private static final String SHELLCHECK_OUTPUT_FORMAT = "-fjson";
    private static final String SHELLCHECK_EXCLUDE_RULES = "-e";

    private final List<String> excludeRules;

    ShellcheckExecutor(List<String> excludeRules) {
        this.excludeRules = excludeRules;
    }

    String runOnFile(String filePath) {
        log.info("Review on file: {}", filePath);
        return new ExternalProcess().executeCommand(buildParams(filePath));
    }

    @NotNull
    private String[] buildParams(String filePath) {
        List<String> shellcheckArgs = ImmutableList.of(
                SHELLCHECK_EXECUTABLE,
                SHELLCHECK_OUTPUT_FORMAT);
        List<String> filePathArg = ImmutableList.of(filePath);
        List<String> excludeRulesArg = getExcludeRulesArgs();
        List<String> allArgs = Lists.newArrayList(Iterables.concat(shellcheckArgs, filePathArg, excludeRulesArg));
        return allArgs.toArray(new String[allArgs.size()]);
    }

    private List<String> getExcludeRulesArgs() {
        if (excludeRules.isEmpty()) {
            return ImmutableList.of();
        }
        return ImmutableList.of(SHELLCHECK_EXCLUDE_RULES + StringUtils.join(excludeRules, ","));
    }
}
