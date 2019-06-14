package pl.touk.sputnik.connector;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectorFacadeFactoryTest {

    private ConnectorFacadeFactory connectorFacadeFactory = new ConnectorFacadeFactory();

    @Test
    void shouldBuildConnector() {
        GerritFacadeBuilder gerritFacadeBuilderMock = mock(GerritFacadeBuilder.class);
        GerritFacade gerritFacadeMock = mock(GerritFacade.class);
        Configuration config = mock(Configuration.class);
        when(gerritFacadeBuilderMock.build(config)).thenReturn(gerritFacadeMock);
        connectorFacadeFactory.gerritFacadeBuilder = gerritFacadeBuilderMock;

        ConnectorFacade facade = connectorFacadeFactory.build(ConnectorType.GERRIT, config);

        assertThat(facade).isEqualTo(gerritFacadeMock);
    }
}