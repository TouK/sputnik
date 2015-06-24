package pl.touk.sputnik.processor.codenarc;

import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.analyzer.SourceAnalyzer;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import java.util.List;

class CodeNarcRunnerBuilder {
    public CodeNarcRunner prepareCodeNarcRunner(List<String> reviewFiles, @NotNull Configuration configuration) {
        CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setRuleSetFiles(configuration.getProperty(GeneralOption.CODE_NARC_RULESET));
        codeNarcRunner.setSourceAnalyzer(createSourceAnalyzer(reviewFiles, configuration));
        return codeNarcRunner;
    }

    private SourceAnalyzer createSourceAnalyzer(List<String> reviewFiles, @NotNull Configuration configuration) {
        FilesystemSourceAnalyzer sourceAnalyzer = new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(".");
        sourceAnalyzer.setIncludes(createFileList(reviewFiles));
        sourceAnalyzer.setExcludes(configuration.getProperty(GeneralOption.CODE_NARC_EXCLUDES));
        return sourceAnalyzer;
    }

    private String createFileList(List<String> reviewFiles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String filesPath : reviewFiles) {
            stringBuilder.append("**/").append(filesPath).append(",");
        }
        return stringBuilder.toString();
    }
}
