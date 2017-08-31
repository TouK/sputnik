package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Config;
import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.FileProcessListener;
import io.gitlab.arturbosch.detekt.api.Finding;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.Collection;
import java.util.List;

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
    public void onFinish(List<? extends KtFile> list, Detektion detektion) {
        log.debug("Processed {} files and found {} problems", list.size(), countProblems(detektion));
    }

    private int countProblems(Detektion detektion) {
        int problems = 0;
        Collection<List<Finding>> findings = detektion.getFindings().values();
        for (List<Finding> finding : findings) {
            problems += finding.size();
        }
        return problems;
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
