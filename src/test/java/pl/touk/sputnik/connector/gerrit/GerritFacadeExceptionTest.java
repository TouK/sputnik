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

    private static final String CHANGE_ID = "changeId";
    private static final String REVISION_ID = "revisionId";
    private static final String TAG = "tag";

    @Mock
    private GerritApi gerritApi;

    @Mock
    private Changes changes;

    @Test
    void shouldWrapConnectorException() throws Exception {
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id(CHANGE_ID)).thenThrow(new RuntimeException("Connection refused"));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset(CHANGE_ID, REVISION_ID, TAG));

        Throwable thrown = catchThrowable(gerritFacade::listFiles);

        assertThat(thrown)
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when listing files");
    }

}
