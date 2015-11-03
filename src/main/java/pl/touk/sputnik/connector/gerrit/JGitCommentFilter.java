package pl.touk.sputnik.connector.gerrit;

import static java.lang.Integer.parseInt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

@Slf4j
public class JGitCommentFilter implements CommentFilter {

	private final String commitSha1;

	private Map<String, FileDiff> modifiedLines;

	public JGitCommentFilter(String commitId) {
		commitSha1 = commitId;
	}

	@Override
	public boolean filter(String filePath, int line) {
		FileDiff diff = modifiedLines == null ? null : modifiedLines.get(filePath);
		return diff != null && !diff.getModifiedLines().contains(line);
	}

	public JGitCommentFilter init() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();

			log.info("Retreive commit {} in git repository", commitSha1);
			ObjectId commitId = repository.resolve(commitSha1);
			RevWalk walk = new RevWalk(repository);
			RevCommit commit = walk.parseCommit(commitId);

			RevCommit[] parents = commit.getParents();

			Map<String, FileDiff> diffs = new HashMap<>();
			for (RevCommit parent : parents) {
				log.info("Compute diff between {} and {}", parent.getId(), commit.getId());
				ObjectReader reader = repository.newObjectReader();
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset(reader, walk.parseCommit(parent.getId()).getTree());
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, commit.getTree());

				ByteArrayOutputStream os = new ByteArrayOutputStream();
				DiffFormatter diffFormatter = new DiffFormatter(os);
				diffFormatter.setRepository(repository);
				diffFormatter.setContext(0);
				diffFormatter.format(oldTreeIter, newTreeIter);

				String unifiedDiff = new String(os.toByteArray());
				log.debug(unifiedDiff);
				Scanner scanner = new Scanner(unifiedDiff);
				FileDiff currentFileDiff = null;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.startsWith("+++ ")) {
						String fileName = line.split(" ")[1].substring(2);
						currentFileDiff = diffs.get(fileName);
						if (currentFileDiff == null) {
							currentFileDiff = new FileDiff(fileName);
							diffs.put(fileName, currentFileDiff);
						}
					}
					if (line.startsWith("@@")) {
						String[] split = line.split(" ")[2].split(",");
						if (currentFileDiff != null) {
							currentFileDiff.addHunk(parseInt(split[0]), split.length > 1 ? parseInt(split[1]) : 0);
						}
					}

				}
				scanner.close();
				diffFormatter.close();
				reader.close();
			}
			walk.close();
			repository.close();
			modifiedLines = diffs;
		} catch (Exception e) {
			modifiedLines = null;
			log.error("Cannot retreive modified line by file", e);
		}
		return this;
	}
}
