package pl.touk.sputnik.connector;

import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
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
        GerritFacadeBuilder gerritFacadeBuilderMock = mock(GerritFacadeBuilder.class);
        GerritFacade gerritFacadeMock = mock(GerritFacade.class);
        when(gerritFacadeBuilderMock.build()).thenReturn(gerritFacadeMock);
        connectorFacadeFactory.gerritFacadeBuilder = gerritFacadeBuilderMock;
        ConnectorFacade facade = connectorFacadeFactory.build("gerrit");

        assertThat(facade).isEqualTo(gerritFacadeMock);
    }

    @Test
    public void shouldThrowIfKeyIsUnknown() {
        catchException(connectorFacadeFactory).build("unknown");

        assertThat((Throwable) caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Connector unknown is not supported");
    }

}