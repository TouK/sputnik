package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.Changes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerritFacadeExceptionTest {

    @Mock
    private GerritApi gerritApi;

    @Mock
    private Changes changes;

    @Mock
    private CommentFilter commentFilter;

    @Test
    void shouldWrapConnectorException() throws Exception {
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id("foo")).thenThrow(new RuntimeException("Connection refused"));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset("foo", "bar"), commentFilter);

        Throwable thrown = catchThrowable(gerritFacade::listFiles);

        assertThat(thrown)
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when listing files");
    }

}
