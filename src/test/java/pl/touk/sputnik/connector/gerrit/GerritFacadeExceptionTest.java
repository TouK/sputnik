package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.restapi.RestApiException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import pl.touk.sputnik.review.Review;

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
    void listFiles_shouldWrapConnectorException() throws Exception {
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id(CHANGE_ID)).thenThrow(new RuntimeException("Connection refused"));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset(CHANGE_ID, REVISION_ID, TAG), GerritOptions.empty());

        Throwable thrown = catchThrowable(gerritFacade::listFiles);

        assertThat(thrown)
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when listing files");
    }

    @Test
    void publish_shouldWrapConnectorException() throws Exception {
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id(CHANGE_ID)).thenThrow(new RuntimeException("Connection refused"));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset(CHANGE_ID, REVISION_ID, TAG), GerritOptions.empty());

        Throwable thrown = catchThrowable(() -> gerritFacade.publish(new Review(new ArrayList<>(), null)));

        assertThat(thrown)
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when setting review");
    }

    @Test
    void getRevision_shouldWrapRestApiException() throws Exception {
        when(gerritApi.changes()).thenReturn(changes);
        when(changes.id(CHANGE_ID)).thenThrow(RestApiException.wrap("Connection refused", new RuntimeException("Something bad happened")));
        GerritFacade gerritFacade = new GerritFacade(gerritApi, new GerritPatchset(CHANGE_ID, REVISION_ID, TAG), GerritOptions.empty());

        Throwable thrown = catchThrowable(gerritFacade::getRevision);

        assertThat(thrown)
                .isInstanceOf(GerritException.class)
                .hasMessageContaining("Error when retrieve modified lines");
    }
}
