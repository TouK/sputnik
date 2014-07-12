package pl.touk.sputnik.processor.codenarc;

import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.analyzer.SourceAnalyzer;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;

class CodeNarcRunnerBuilder {
    public CodeNarcRunner prepareCodeNarcRunner(Review review) {
        CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setRuleSetFiles(ConfigurationHolder.instance().getProperty(GeneralOption.CODE_NARC_RULESET));
        codeNarcRunner.setSourceAnalyzer(createSourceAnalyzer(review));
        return codeNarcRunner;
    }

    private SourceAnalyzer createSourceAnalyzer(Review review) {
        FilesystemSourceAnalyzer sourceAnalyzer = new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(".");
        sourceAnalyzer.setIncludes(createFileList(review));
        return sourceAnalyzer;
    }

    private String createFileList(Review review) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String filesPath : review.getIOFilenames()) {
            stringBuilder.append("**/").append(filesPath).append(",");
        }
        return stringBuilder.toString();
    }
}
