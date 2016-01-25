package pl.touk.sputnik.connector.saas;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.HttpConnectorEnv;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.*;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SaasFacadeTest extends HttpConnectorEnv {

    private static Integer SOME_PULL_REQUEST_ID = 12314;
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT = "proj";

    private static final Map<String, String> GITHUB_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID.toString(),
            "connector.repository", SOME_REPOSITORY,
            "connector.project", SOME_PROJECT
    );

    private SaasFacade saasFacade;
    private Configuration config;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT);

    @Before
    public void setUp() {
        config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpConfig("saas"), GITHUB_PATCHSET_MAP);
        saasFacade = new SaasFacadeBuilder().build(config);
    }

    @Test
    public void shouldListFiles() throws Exception {
        stubGet(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/files",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/saas-files.json");

        List<ReviewFile> files = saasFacade.listFiles();

        assertThat(files).extracting("reviewFilename").containsOnly("src/main/java/TestFile.java", "src/main/java/TestFile2.java");
    }

    @Test
    public void shouldPublishReview() throws Exception {
        stubPost(urlEqualTo(String.format(
                "%s/api/github/%s/%s/pulls/%s/violations",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)), "/json/saas-files.json");

        String filename = "src/main/java/Main.java";
        Review review = new Review(ImmutableList.of(new ReviewFile(filename)), ReviewFormatterFactory.get(config));
        review.addError("checkstyle", new Violation(filename, 1, "error message", Severity.ERROR));
        review.getMessages().add("Total 1 violations found");

        saasFacade.publish(review);

        verify(1, postRequestedFor(urlMatching(String.format("%s/api/github/%s/%s/pulls/%s/violations",
                FacadeConfigUtil.PATH, SOME_PROJECT, SOME_REPOSITORY, SOME_PULL_REQUEST_ID))));
    }
}
