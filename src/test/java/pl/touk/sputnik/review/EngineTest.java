package pl.touk.sputnik.review;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.ConnectorFacade;
import pl.touk.sputnik.Patchset;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EngineTest {

    @InjectMocks
    private Engine fixture;

    @Mock
    private ConnectorFacade connectorFacade;

    @Mock
    private Patchset patchset;

    @Mock
    private ReviewFile reviewFile;

    @Before
    public void setUp() throws Exception {
        Configuration.instance().setConfigurationResource("engine.properties");
        Configuration.instance().init();
    }

    @After
    public void tearDown() throws Exception {
        Configuration.instance().reset();
    }

    @Test
    public void shouldRunEngine() {
        //given
        when(connectorFacade.createPatchset()).thenReturn(patchset);
        when(connectorFacade.listFiles(patchset)).thenReturn(ImmutableList.of(reviewFile));
        when(reviewFile.getComments()).thenReturn(ImmutableList.of(new Comment(1, "test")));

        //when
        fixture.run(connectorFacade);

        //then
        ArgumentCaptor<ReviewInput> captor = ArgumentCaptor.forClass(ReviewInput.class);
        verify(connectorFacade).setReview(eq(patchset), captor.capture());
        assertThat(captor.getValue().comments).hasSize(1);
    }

}