package pl.touk.sputnik.connector.local;

import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.NullOutputStream;

import java.io.IOException;

public class LocalFacadeBuilder {
    public LocalFacade build() {
        try (Repository repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build()) {
            try (DiffFormatter diffFormatter = new DiffFormatter(NullOutputStream.INSTANCE)) {
                diffFormatter.setRepository(repository);
                return new LocalFacade(repository, diffFormatter, new LocalFacadeOutput());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting git repository", e);
        }
    }
}
