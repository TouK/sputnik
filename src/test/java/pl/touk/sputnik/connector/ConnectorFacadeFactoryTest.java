package pl.touk.sputnik.connector;

import org.junit.Before;
import org.junit.Test;

import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectorFacadeFactoryTest {

    private ConnectorFacadeFactory connectorFacadeFactory;

    @Before
    public void setUp() {
        connectorFacadeFactory = new ConnectorFacadeFactory();
    }

    @Test
    public void shouldBuildConnector() {
        // given
        GerritFacadeBuilder gerritFacadeBuilderMock = mock(GerritFacadeBuilder.class);
        GerritFacade gerritFacadeMock = mock(GerritFacade.class);
        when(gerritFacadeBuilderMock.build()).thenReturn(gerritFacadeMock);
        connectorFacadeFactory.gerritFacadeBuilder = gerritFacadeBuilderMock;

        // when
        ConnectorFacade facade = connectorFacadeFactory.build(ConnectorType.GERRIT);

        // then
        assertThat(facade).isEqualTo(gerritFacadeMock);
    }
}