package pl.touk.sputnik.exec;

import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ExternalProcess {

    public ProcessExecutor executor() {
        return new ProcessExecutor();
    }

    public String executeCommand(String... args) {
        try {
            log.debug("Executing command " + Arrays.asList(args));
            return executor().command(args)
                    .timeout(60, TimeUnit.SECONDS)
                    .redirectError(Slf4jStream.of(getClass()).asInfo())
                    .readOutput(true)
                    .execute()
                    .outputUTF8();
        } catch (Exception e) {
            log.warn("Exception while calling command " + Arrays.asList(args) + ": " + e);
            throw new ExternalProcessException(e);
        }
    }
}
