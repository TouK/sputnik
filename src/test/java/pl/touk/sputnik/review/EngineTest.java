package pl.touk.sputnik.review;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.GerritPatchset;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EngineTest {

    @InjectMocks
    private Engine fixture;

    @Mock
    private ConnectorFacade connectorFacade;

    @Mock
    private GerritPatchset patchset;

    @Mock
    private ReviewFile reviewFile;

    private static final Map<String, String> GERRIT_CONFIG_MAP = ImmutableMap.of(
            "gerrit.host", "localhost",
            "gerrit.port", "1234",
            "gerrit.username", "user",
            "gerrit.password", "pass",
            "gerrit.useHttps", "false"
    );

    private static final Map<String, String> GERRIT_PATCHSET_MAP = ImmutableMap.of(
            "cli.connector", "gerrit",
            "cli.changeId", "123",
            "cli.revisionId", "456",
            "gerrit.projectKey", "mykey"
    );

    @Before
    public void setUp() throws Exception {
        new ConfigurationSetup().setUp(GERRIT_CONFIG_MAP, GERRIT_PATCHSET_MAP);
    }

    @After
    public void tearDown() throws Exception {
        Configuration.instance().reset();
    }

    @Test
    public void shouldRunEngine() {
        //given
        when(connectorFacade.listFiles()).thenReturn(ImmutableList.of(reviewFile));
        when(reviewFile.getComments()).thenReturn(ImmutableList.of(new Comment(1, "test")));

        //when
        fixture.run();

        //then
        ArgumentCaptor<ReviewInput> captor = ArgumentCaptor.forClass(ReviewInput.class);
        verify(connectorFacade).setReview(captor.capture());
        assertThat(captor.getValue().comments).hasSize(1);
    }

}
