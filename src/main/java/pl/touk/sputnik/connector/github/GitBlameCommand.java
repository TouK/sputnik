package pl.touk.sputnik.connector.github;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitBlameCommand {

    public Map<String, List<Integer>> gitBlame(String filename, String sha) throws IOException, GitAPIException {
        return gitBlame(filename, sha, ".");
    }

    public Map<String, List<Integer>> gitBlame(String filename, String sha, String startPath) throws IOException, GitAPIException {
        Map<String, List<Integer>> result = new HashMap<>();
        Repository repository = openJGitRepository(startPath);

        BlameCommand blamer = new BlameCommand(repository);
        ObjectId commitID = repository.resolve(sha);
        blamer.setStartCommit(commitID);
        blamer.setFilePath(filename);
        BlameResult blame = blamer.call();

        // read the number of lines from the commit to not look at changes in the working copy
        int lines = countFiles(repository, commitID, filename);
        for (int i = 0; i < lines; i++) {
            RevCommit commit = blame.getSourceCommit(i);
            String lineSha = commit.toString().split(" ")[1];
            addToMap(result, i, lineSha);
        }

        repository.close();
        return result;
    }

    private void addToMap(Map<String, List<Integer>> result, int lineNo, String lineSha) {
        if (!result.containsKey(lineSha)) {
            result.put(lineSha, new ArrayList<Integer>());
        }
        result.get(lineSha).add(lineNo);
    }

    private int countFiles(Repository repository, ObjectId commitID, String name) throws IOException {
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitID);
        RevTree tree = commit.getTree();

        // now try to find a specific file
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(name));
        if (!treeWalk.next()) {
            throw new IllegalStateException("Did not find expected file '" + name + "'");
        }

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(objectId);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // and then one can the loader to read the file
        loader.copyTo(stream);

        revWalk.dispose();

        return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray())).size();
    }

    private Repository openJGitRepository(String startPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir(new File(startPath)) // scan up the file system tree
                .build();
    }
}
