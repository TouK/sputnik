package pl.touk.sputnik.connector.gerrit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GerritFacadeExceptionTest {

    @Mock
    GerritConnector gerritConnector;

    @InjectMocks
    GerritFacade fixture;

    @Test
    public void shouldWrapConnectorException() throws Exception {
        //given
        when(gerritConnector.listFiles()).thenThrow(new IOException("Connection refused"));

        //when
        catchException(fixture).listFiles();

        //then
        assertThat(caughtException()).isInstanceOf(GerritException.class);
        assertThat(caughtException()).hasMessageContaining("Error listing files");
    }

}
