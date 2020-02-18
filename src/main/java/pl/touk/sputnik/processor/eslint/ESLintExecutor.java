package pl.touk.sputnik.processor.eslint;

import com.google.common.collect.Lists;
import pl.touk.sputnik.exec.ExternalProcess;

import java.util.Arrays;
import java.util.List;

class ESLintExecutor {

    private static final String[] ESLINT_OUTPUT_FORMAT = {"--format", "json"};
    private static final String ESLINT_RCFILE_NAME = "--config";
    private static final String ESLINT_RESOLVE_PLUGINS_RELATIVE_TO = "--resolve-plugins-relative-to";

    private final String eslintRcFile;
    private final String eslintExecutable;
    private final String pluginsFolder;

    ESLintExecutor(String eslintRcFile, String eslintExecutable, String pluginsFolder) {
        this.eslintRcFile = eslintRcFile;
        this.eslintExecutable = eslintExecutable;
        this.pluginsFolder = pluginsFolder;
    }

    String runOnFile(String filePath) {
        return new ExternalProcess().executeCommand(buildParams(filePath));
    }

    private String[] buildParams(String filePath) {
        List<String> args = Lists.newArrayList(eslintExecutable);
        args.addAll(Arrays.asList(ESLINT_OUTPUT_FORMAT));
        if (pluginsFolder != null) {
            args.addAll(Arrays.asList(ESLINT_RESOLVE_PLUGINS_RELATIVE_TO, pluginsFolder));
        }
        if (eslintRcFile != null) {
            args.addAll(Arrays.asList(ESLINT_RCFILE_NAME, eslintRcFile));
        }
        args.add(filePath);
        return args.toArray(new String[args.size()]);
    }
}
