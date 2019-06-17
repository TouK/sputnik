package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Config;
import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.FileProcessListener;
import io.gitlab.arturbosch.detekt.api.Finding;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.List;
import java.util.Map;

@Slf4j
class LoggingFileProcessor implements FileProcessListener {
    @Override
    public void onStart(List<? extends KtFile> list) {
        log.debug("Found {} files for review", list.size());
    }

    @Override
    public void onProcess(KtFile ktFile) {
        log.debug("Processing {}", ktFile.getName());
    }

    @Override
    public void onProcessComplete(KtFile ktFile, Map<String, ? extends List<? extends Finding>> map) {
        log.debug("Processed {} and found {} problems", ktFile.getName(), countProblems(map));
    }

    @Override
    public void onFinish(List<? extends KtFile> list, Detektion detektion) {
        log.debug("Processed {} files and found {} problems", list.size(), countProblems(detektion));
    }

    private int countProblems(Detektion detektion) {
        return countProblems(detektion.getFindings());
    }

    private int countProblems(Map<String, ? extends List<? extends Finding>> findings) {
        return findings.values().stream().mapToInt(List::size).sum();
    }

    @NotNull
    @Override
    public String getId() {
        return "logging processor";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public void init(Config config) {

    }
}
