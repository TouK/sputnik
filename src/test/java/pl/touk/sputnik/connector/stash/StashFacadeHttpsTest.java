package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.HttpClientFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.ReviewFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class StashFacadeHttpsTest {

    private static Integer PORT = 8089;
    private static Integer HTTPS_PORT = 8443;
    private static final ImmutableMap<String, String> STASH_CONFIG_MAP = ImmutableMap.of(
            "stash.host", "localhost",
            "stash.port", HTTPS_PORT.toString(),
            "stash.username", "user",
            "stash.password", "pass",
            "stash.useHttps", "true"
    );
    private static final ImmutableMap<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", "12",
            "stash.repositorySlug", "myproject",
            "stash.projectKey", "mykey"
    );

    private StashFacade stashFacade;
    private WireMockServer wireMockServer;
    private HttpClient httpClient;


    @After
    public void serverShutdown() {
        wireMockServer.stop();
    }

    @Before
    public void setUp() {
        Map<String, String> joinedMap = new HashMap<>();
        joinedMap.putAll(STASH_CONFIG_MAP);
        joinedMap.putAll(STASH_PATCHSET_MAP);
        new ConfigurationSetup().setUp(joinedMap);
        stashFacade = new StashFacadeBuilder().build();
        startServerWithDefaultKeystore();
    }

    public void startServerWithDefaultKeystore() {
        //this is wiremock's default keystore
        WireMockConfiguration config = wireMockConfig().port(PORT).httpsPort(HTTPS_PORT);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();
        WireMock.configure();
        httpClient = HttpClientFactory.createClient();
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        configureFor("localhost", PORT);
        String changesUrl = "/rest/api/1.0/projects/mykey/repos/myproject/pull-requests/12/changes";
        stubFor(get(urlEqualTo(changesUrl))
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-changes.json")))));

        List<ReviewFile> files = stashFacade.listFiles();
        assertThat(files).hasSize(4);
    }

    private String url(String path) {
        return String.format("https://localhost:%d%s", HTTPS_PORT, path);
    }

    private void getAndAssertUnderlyingExceptionInstanceClass(String url, Class<?> expectedClass) {
        boolean thrown = false;
        try {
            contentFor(url);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                Assert.assertThat(e.getCause(), instanceOf(expectedClass));
            } else {
                Assert.assertThat(e, instanceOf(expectedClass));
            }

            thrown = true;
        }

        assertTrue("No exception was thrown", thrown);
    }

    private String contentFor(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        HttpResponse response = httpClient.execute(get);
        String content = EntityUtils.toString(response.getEntity());
        return content;
    }
}
