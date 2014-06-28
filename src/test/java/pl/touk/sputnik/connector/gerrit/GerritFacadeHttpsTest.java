package pl.touk.sputnik.connector.gerrit;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.connector.stash.StashFacadeBuilder;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GerritFacadeHttpsTest {

    private static String SOME_CHANGE_ID = "12314";
    private static String SOME_REVISION_ID = "12314";

    private static final Map<String, String> GERRIT_PATCHSET_MAP = ImmutableMap.of(
            "cli.changeId", SOME_CHANGE_ID,
            "cli.revisionId", SOME_REVISION_ID
    );

    private GerritFacade gerritFacade;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT, FacadeConfigUtil.HTTPS_PORT);

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpsConfig("gerrit"), GERRIT_PATCHSET_MAP);
        gerritFacade = new GerritFacadeBuilder().build();
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        String url = String.format("/a/changes/%s/revisions/%s/files/", SOME_CHANGE_ID, SOME_REVISION_ID);
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/gerrit-changes.json")))));

        List<ReviewFile> files = gerritFacade.listFiles();

        assertThat(files).hasSize(2);
    }
}
