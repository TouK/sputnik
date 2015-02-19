package pl.touk.sputnik.connector.github;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExtractDiffFromCommits {

    String extract(String branchName, String oldHash) throws IOException, GitAPIException {
        Git git = Git.open(new File("./"));

        ObjectId headId = git.getRepository().resolve(branchName + "^{tree}");
        ObjectId oldId = git.getRepository().resolve(oldHash + "^{tree}");

        ObjectReader reader = git.getRepository().newObjectReader();

        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldId);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headId);

        List<DiffEntry> diffs= git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter diffFormatter = new DiffFormatter(out);
        diffFormatter.setRepository(git.getRepository());

        StringBuilder sb = new StringBuilder();
        for (DiffEntry diff : diffs) {
            diffFormatter.format(diff);
            sb.append(out.toString("UTF-8"));
            out.reset();
        }
        return sb.toString();
    }
}
