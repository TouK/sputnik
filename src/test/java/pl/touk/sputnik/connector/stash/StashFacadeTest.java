package pl.touk.sputnik.connector.stash;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.List;
import java.util.Map;

public class StashFacadeTest {

    private static String SOME_PULL_REQUEST_ID = "12314";
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT_KEY = "key";

    private static final Map<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID,
            "connector.repositorySlug", SOME_REPOSITORY,
            "connector.projectKey", SOME_PROJECT_KEY
    );

    private StashFacade stashFacade;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT);

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build();
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

        Review review = new Review(ImmutableList.of(new ReviewFile(filename)));
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

    private void stubGet(UrlMatchingStrategy url, String responseFile) throws Exception {
        stubFor(get(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile)))));
    }

    private void stubPost(UrlMatchingStrategy url, String responseFile) throws Exception {
        stubFor(post(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile)))));
    }
}
