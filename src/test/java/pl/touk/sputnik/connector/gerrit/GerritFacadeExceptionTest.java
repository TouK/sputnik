package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.restapi.RestApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GerritFacadeExceptionTest {

    @Mock
    GerritApi gerritApi;

    @Test
    public void shouldWrapConnectorException() throws Exception {
        //given
        Changes changes = mock(Changes.class);
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id("foo")).thenThrow(new RestApiException("Connection refused"));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset("foo", "bar"));

        //when
        catchException(gerritFacade).listFiles();

        //then
        assertThat(caughtException())
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when listing files");
    }

}
