package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.touk.sputnik.HttpConnectorEnv;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.*;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class StashFacadeTest extends HttpConnectorEnv {

    private static String SOME_PULL_REQUEST_ID = "12314";
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT_KEY = "key";

    private static final Map<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID,
            "connector.repository", SOME_REPOSITORY,
            "connector.project", SOME_PROJECT_KEY
    );

    private StashFacade stashFacade;
    private Configuration config;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT);

    @Before
    public void setUp() {
        config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build(config);
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        stubGet(urlEqualTo(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-changes.json");

        List<ReviewFile> files = stashFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("project/RecoBuild.scala", "project/RecoRelease.scala",
                "reco-analyzer/src/main/scala/com/allegrogroup/reco/analyzer/spark/ImportUserRecommendationsToCassandraSparkJob.scala",
                "version.sbt");
    }

    @Test
    public void shouldReturnDiffAsMapOfLines() throws Exception {
        stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff.json");

        SingleFileChanges singleFileChanges = stashFacade.changesForSingleFile("src/main/java/Main.java");
        assertThat(singleFileChanges.getFilename()).isEqualTo("src/main/java/Main.java");
        assertThat(singleFileChanges.getChangeType(1)).isEqualTo(ChangeType.ADDED);
        assertThat(singleFileChanges.getChangeType(2)).isEqualTo(ChangeType.ADDED);
    }

    @Test
    public void shouldNotAddTheSameCommentMoreThanOnce() throws Exception {
        String filename = "src/main/java/Main.java";

        stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff-empty.json");

        stubPost(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff-empty.json");

        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("scalastyle", new Violation(filename, 1, "error message", Severity.ERROR));
        review.getMessages().add("Total 1 violations found");

        stashFacade.setReview(review);

        stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff.json");

        stashFacade.setReview(review);

        // First review : 1 comment on file and 1 comment on summary message
        // Second review: 1 comment on summary message
        verify(3, postRequestedFor(urlEqualTo(String.format("%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID))));
    }

    @Test
    public void shouldSkipDeletedFiles() throws Exception {
        stubGet(urlEqualTo(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-changes-deleted-file.json");

        List<ReviewFile> files = stashFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/example/App2.java");
    }

    @Parameters({"/json/stash-diff-no-file-comments.json, 1",
            "/json/stash-diff-with-file-comment.json, 0"})
    @TestCaseName("file comment should be send {2} time(s)")
    @Test
    public void fileComments(String diffFile, int expectedNumberOfCommentsSent) throws Exception {
        String pullRequestResourceUrl = String.format("%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID);
        stubGet(urlMatching(pullRequestResourceUrl + "/diff.*"), diffFile);
        stubGet(urlEqualTo(pullRequestResourceUrl + "/changes"), "/json/stash-changes-for-file-violation.json");
        stubFor(post(urlEqualTo(pullRequestResourceUrl + "/comments")).willReturn(aResponse().withStatus(200)));
        String filename = "src/main/java/com/example/app/Runner.java";
        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("Checkstyle", new Violation(filename, 0, "File does not end with a newline.", Severity.WARNING));
        review.getMessages().add("Total 1 violations found");

        stashFacade.publish(review);

        verify(expectedNumberOfCommentsSent, postRequestedFor(urlEqualTo(pullRequestResourceUrl + "/comments"))
                .withRequestBody(equalToJson("{\"text\":\"[Checkstyle] WARNING: File does not end with a newline.\"," +
                        "\"anchor\":{\"line\":0," +
                        "\"lineType\":\"CONTEXT\"," +
                        "\"fileType\":null," +
                        "\"path\":\"src/main/java/com/example/app/Runner.java\",\n" +
                        "\"srcPath\":\"src/main/java/com/example/app/Runner.java\"}}")));
    }
}
