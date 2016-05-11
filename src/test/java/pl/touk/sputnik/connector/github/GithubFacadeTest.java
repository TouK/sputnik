package pl.touk.sputnik.connector.github;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jcabi.github.*;
import com.jcabi.immutable.Array;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.Provider;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.connector.Patchset;
import pl.touk.sputnik.review.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GithubFacadeTest {

    private static Integer SOME_PULL_REQUEST_ID = 12314;
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT = "proj";

    private static final Map<String, String> GITHUB_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
            "connector.repository", SOME_REPOSITORY,
            "connector.owner", SOME_PROJECT
    );

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Repo repo;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Pull pull;

    @Mock
    private Commit commit;

    @Mock
    private Commits commits;

    @Mock
    private Statuses statuses;

    private GithubFacade githubFacade;
    private Configuration config;

    @Before
    public void setUp() throws IOException {
        when(repo.pulls().get(SOME_PULL_REQUEST_ID)).thenReturn(pull);
        when(pull.commits()).thenReturn(pullCommits());
        when(pull.repo().git().commits()).thenReturn(commits);

        config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("github"), GITHUB_PATCHSET_MAP);
        githubFacade = new GithubFacade(repo, new Patchset(SOME_PULL_REQUEST_ID, projectPath(), Provider.GITHUB));
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        when(pull.files()).thenReturn(pullFiles());

        List<ReviewFile> files = githubFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("1.java");
    }

    @Test
    public void shouldAddIssue() throws Exception {
        when(commit.sha()).thenReturn("sha1");
        when(commits.statuses("sha1")).thenReturn(statuses);

        String filename = "src/main/java/Main.java";
        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("checkstyle", new Violation(filename, 1, "error message", Severity.ERROR));
        review.getMessages().add("Total 1 violations found");

        githubFacade.setReview(review);

        verify(statuses).create(any(Statuses.StatusCreate.class));
    }

    private Iterable<Commit> pullCommits() {
        return new Array<>(commit);
    }


    private List<JsonObject> pullFiles() {
        return Json.createArrayBuilder().add(Json.createObjectBuilder().add("filename", "1.java").build()).build().getValuesAs(JsonObject.class);
    }

    private String projectPath() {
        return String.format("%s/%s", SOME_PROJECT, SOME_REPOSITORY);
    }
}
