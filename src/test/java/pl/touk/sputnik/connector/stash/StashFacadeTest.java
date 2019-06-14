package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.HttpConnectorEnv;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class StashFacadeTest {

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
    private WireMockServer wireMockServer;
    private HttpConnectorEnv httpConnectorEnv;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(FacadeConfigUtil.HTTP_PORT).httpsPort(FacadeConfigUtil.HTTPS_PORT));
        wireMockServer.start();

        httpConnectorEnv = new HttpConnectorEnv(wireMockServer);

        config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build(config);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldGetChangeInfo() throws Exception {
        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-changes.json");

        List<ReviewFile> files = stashFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("project/RecoBuild.scala", "project/RecoRelease.scala",
                "reco-analyzer/src/main/scala/com/allegrogroup/reco/analyzer/spark/ImportUserRecommendationsToCassandraSparkJob.scala",
                "version.sbt");
    }

    @Test
    void shouldReturnDiffAsMapOfLines() throws Exception {
        httpConnectorEnv.stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff.json");

        SingleFileChanges singleFileChanges = stashFacade.changesForSingleFile("src/main/java/Main.java");
        assertThat(singleFileChanges.getFilename()).isEqualTo("src/main/java/Main.java");
        assertThat(singleFileChanges.getChangeType(1)).isEqualTo(ChangeType.ADDED);
        assertThat(singleFileChanges.getChangeType(2)).isEqualTo(ChangeType.ADDED);
    }

    @Test
    void shouldNotAddTheSameCommentMoreThanOnce() throws Exception {
        String filename = "src/main/java/Main.java";

        httpConnectorEnv.stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff-empty.json");

        httpConnectorEnv.stubPost(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff-empty.json");

        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("scalastyle", new Violation(filename, 1, "error message", Severity.ERROR));
        review.getMessages().add("Total 1 violations found");

        stashFacade.setReview(review);

        httpConnectorEnv.stubGet(urlMatching(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/diff.*",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-diff.json");

        stashFacade.setReview(review);

        // First review : 1 comment on file and 1 comment on summary message
        // Second review: 1 comment on summary message
        wireMockServer.verify(3, postRequestedFor(urlEqualTo(String.format("%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/comments",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID))));
    }

    @Test
    void shouldSkipDeletedFiles() throws Exception {
        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/stash-changes-deleted-file.json");

        List<ReviewFile> files = stashFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/example/App2.java");
    }

}
