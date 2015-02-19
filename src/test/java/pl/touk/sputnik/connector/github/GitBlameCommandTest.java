package pl.touk.sputnik.connector.github;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class GitBlameCommandTest {

    @Test
    public void shouldOutputBlame() throws Exception {
        // given
        String gitRepositoryPath = "src/test/resources/sample-git-repo";
        String filename = "README.md";
        String sha = "0d527b526dd9382bdc9d52c57c8f0b2b850a04aa";

        // when
        Map<String, List<Integer>> blameMap = new GitBlameCommand().gitBlame(filename, sha, gitRepositoryPath);

        // then
        assertThat(blameMap).contains(
                entry("e457b90e6e36bbc62f0f1c60e0ec21f8cac3b098", Lists.newArrayList(0)),
                entry("0a4c3961063c681979999cdf155562ff6e97fe7e", asList(Range.closed(1, 22))),
                entry("0d527b526dd9382bdc9d52c57c8f0b2b850a04aa", asList(Range.closed(23, 31))));

    }

    private List<Integer> asList(Range<Integer> range) {
        Set<Integer> rangedSet = ContiguousSet.create(range, DiscreteDomain.integers());
        List<Integer> result = Lists.newArrayList();
        result.addAll(rangedSet);
        return result;
    }
}
