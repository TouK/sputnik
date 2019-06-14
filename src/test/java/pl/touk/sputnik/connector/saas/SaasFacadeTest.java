package pl.touk.sputnik.connector.saas;

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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static pl.touk.sputnik.configuration.Provider.GITHUB;

class SaasFacadeTest {

    private static final Integer SOME_PULL_REQUEST_ID = 12314;
    private static final String SOME_REPOSITORY = "repo";
    private static final String SOME_PROJECT = "proj";
    private static final String SOME_API_KEY = "my_api_key";

    private static final Map<String, String> GITHUB_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
            "cli.apiKey", SOME_API_KEY,
            "cli.provider", GITHUB.getName(),
            "connector.repository", SOME_REPOSITORY,
            "connector.project", SOME_PROJECT
    );

    private SaasFacade saasFacade;
    private Configuration config;
    private WireMockServer wireMockServer;
    private HttpConnectorEnv httpConnectorEnv;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(FacadeConfigUtil.HTTP_PORT));
        wireMockServer.start();

        httpConnectorEnv = new HttpConnectorEnv(wireMockServer);

        config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("saas"), GITHUB_PATCHSET_MAP);
        saasFacade = new SaasFacadeBuilder().build(config);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldListFiles() throws Exception {
        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/files?key=%s",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID, SOME_API_KEY)), "/json/saas-files.json");

        List<ReviewFile> files = saasFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/TestFile.java", "src/main/java/TestFile2.java");
    }

    @Test
    void shouldPublishReview() throws Exception {
        httpConnectorEnv.stubPost(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/violations?key=%s",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID, SOME_API_KEY)), "/json/saas-files.json");

        String filename = "src/main/java/Main.java";
        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("checkstyle", new Violation(filename, 1, "error message", Severity.ERROR));
        review.getMessages().add("Total 1 violations found");

        saasFacade.publish(review);

        wireMockServer.verify(1, postRequestedFor(urlMatching(String.format("%s/api/github/%s/%s/pulls/%s/violations\\?key=%s",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID, SOME_API_KEY))));
    }

    @Test
    void shouldThrowOnWrongApiKey() {
        SaasFacade saasFacade = buildFacade(ImmutableMap.of(
                "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
                "cli.apiKey", "WRONG_API_KEY",
                "cli.provider", GITHUB.getName(),
                "connector.repository", SOME_REPOSITORY,
                "connector.project", SOME_PROJECT
        ));
        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/files?key=%s",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID, "WRONG_API_KEY")),
                aResponse().withStatus(403));

        Throwable thrown = catchThrowable(saasFacade::listFiles);

        assertThat(thrown).isInstanceOf(SaasException.class);
    }

    @Test
    void shouldHandleEmptyApiKey() throws Exception {
        SaasFacade saasFacade = buildFacade(ImmutableMap.of(
                "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
                "cli.provider", GITHUB.getName(),
                "connector.repository", SOME_REPOSITORY,
                "connector.project", SOME_PROJECT
        ));

        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/files",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/saas-files.json");

        List<ReviewFile> files = saasFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/TestFile.java", "src/main/java/TestFile2.java");
    }

    @Test
    void shouldSendBuildIdIfProvided() throws Exception {
        SaasFacade saasFacade = buildFacade(ImmutableMap.of(
                "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
                "cli.buildId", "11223344",
                "cli.provider", GITHUB.getName(),
                "connector.repository", SOME_REPOSITORY,
                "connector.project", SOME_PROJECT
        ));

        httpConnectorEnv.stubGet(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/files?build_id=%s",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID, "11223344")), "/json/saas-files.json");

        List<ReviewFile> files = saasFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/TestFile.java", "src/main/java/TestFile2.java");
    }

    protected SaasFacade buildFacade(Map<String, String> configMap) {
        Configuration config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("saas"), configMap);
        return new SaasFacadeBuilder().build(config);
    }

}
