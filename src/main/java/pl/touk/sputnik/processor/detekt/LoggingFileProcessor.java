package pl.touk.sputnik.processor.detekt;

import io.gitlab.arturbosch.detekt.api.Config;
import io.gitlab.arturbosch.detekt.api.Detektion;
import io.gitlab.arturbosch.detekt.api.FileProcessListener;
import io.gitlab.arturbosch.detekt.api.Finding;
import io.gitlab.arturbosch.detekt.api.SetupContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.resolve.BindingContext;

import java.util.List;
import java.util.Map;

@Slf4j
class LoggingFileProcessor implements FileProcessListener {
    @Override
    public void onStart( @NotNull List<? extends KtFile> list) {
        log.debug("Found {} files for review", list.size());
    }

    @Override
    public void onStart(@NotNull List<? extends KtFile> list, @NotNull BindingContext bindingContext) {
        onStart(list);
    }

    @Override
    public void onProcess( @NotNull KtFile ktFile) {
        log.debug("Processing {}", ktFile.getName());
    }

    @Override
    public void onProcess(@NotNull KtFile ktFile, @NotNull BindingContext bindingContext) {
        onProcess(ktFile);
    }

    @Override
    public void onProcessComplete( @NotNull KtFile ktFile,  @NotNull Map<String, ? extends List<? extends Finding>> map) {
        log.debug("Processed {} and found {} problems", ktFile.getName(), countProblems(map));
    }

    @Override
    public void onProcessComplete(@NotNull KtFile ktFile, @NotNull Map<String, ? extends List<? extends Finding>> map, @NotNull BindingContext bindingContext) {
        onProcessComplete(ktFile, map);
    }

    @Override
    public void onFinish( @NotNull List<? extends KtFile> list,  @NotNull Detektion detektion) {
        log.debug("Processed {} files and found {} problems", list.size(), countProblems(detektion));
    }

    @Override
    public void onFinish(@NotNull List<? extends KtFile> list, @NotNull Detektion detektion, @NotNull BindingContext bindingContext) {
        onFinish(list, detektion);
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
    public void init(@NotNull Config config) {

    }

    @Override
    public void init(@NotNull SetupContext setupContext) {

    }
}
