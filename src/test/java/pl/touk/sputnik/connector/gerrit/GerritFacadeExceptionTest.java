package pl.touk.sputnik.connector.gerrit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.Patchset;

import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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
        when(gerritConnector.listFiles(any(Patchset.class))).thenThrow(new IOException("Connection refused"));

        //when
        catchException(fixture).listFiles(new GerritPatchset("a", "b"));

        //then
        assertThat(caughtException()).isInstanceOf(GerritException.class);
        assertThat(caughtException()).hasMessageContaining("Error listing files");
    }

}
