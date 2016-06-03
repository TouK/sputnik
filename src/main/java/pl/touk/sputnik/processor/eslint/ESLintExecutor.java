package pl.touk.sputnik.processor.eslint;

import com.google.common.collect.Lists;
import pl.touk.sputnik.exec.ExternalProcess;

import java.util.Arrays;
import java.util.List;

class ESLintExecutor {

    private static final String ESLINT_EXECUTABLE = "eslint";
    private static final String[] ESLINT_OUTPUT_FORMAT = {"--format", "json"};
    private static final String ESLINT_RCFILE_NAME = "--config";

    private final String eslintRcFile;

    ESLintExecutor(String eslintRcFile) {
        this.eslintRcFile = eslintRcFile;
    }

    String runOnFile(String filePath) {
        return new ExternalProcess().executeCommand(buildParams(filePath));
    }

    private String[] buildParams(String filePath) {
        List<String> args = Lists.newArrayList(ESLINT_EXECUTABLE);
        args.addAll(Arrays.asList(ESLINT_OUTPUT_FORMAT));
        if (eslintRcFile != null) {
            args.addAll(Arrays.asList(ESLINT_RCFILE_NAME, eslintRcFile));
        }
        args.add(filePath);
        return args.toArray(new String[args.size()]);
    }
}
