package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.urswolfer.gerrit.client.rest.http.changes.FileInfoParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class GerritFacadeTest {

    private static final String CHANGE_ID = "changeId";
    private static final String REVISION_ID = "revisionId";
    private static final String TAG = "ci";

    @Mock
    private GerritApi gerritApi;
    @Mock
    private GerritOptions gerritOptions;
    @Mock
    private Configuration configuration;

    @Test
    void shouldParseListFilesResponse() throws IOException, RestApiException {
        List<ReviewFile> reviewFiles = createGerritFacade().listFiles();
        assertThat(reviewFiles).isNotEmpty();
    }

    @Test
    void shouldNotListDeletedFiles() throws IOException, RestApiException {
        List<ReviewFile> reviewFiles = createGerritFacade().listFiles();
        assertThat(reviewFiles).hasSize(1);
    }

    @Test
    void shouldCallGerritApiOnPublish() throws IOException, RestApiException {
        when(gerritOptions.isOmitDuplicateComments()).thenReturn(true);
        Review review = new Review(new ArrayList<>(), new ReviewFormatter(configuration));
        ArgumentCaptor<ReviewInput> reviewInputCaptor = ArgumentCaptor.forClass(ReviewInput.class);

        createGerritFacade().publish(review);

        verify(gerritApi.changes().id(CHANGE_ID).revision(REVISION_ID)).review(reviewInputCaptor.capture());
        assertThat(reviewInputCaptor.getValue().omitDuplicateComments).isTrue();
        assertThat(reviewInputCaptor.getValue().tag).isEqualTo(TAG);
    }

    @Test
    void shouldRevisionApiBeConfigured() throws IOException, RestApiException {
        GerritFacade gerritFacade = createGerritFacade();

        assertThat(gerritFacade.getRevision())
                .isEqualTo(gerritApi.changes().id(CHANGE_ID).revision(REVISION_ID));
    }

    @Test
    void shouldReviewDelegateToPublish() throws IOException, RestApiException {
        Review review = new Review(new ArrayList<>(), new ReviewFormatter(configuration));

        createGerritFacade().setReview(review);

        verify(gerritApi.changes().id(CHANGE_ID).revision(REVISION_ID)).review(any());
    }

    private GerritFacade createGerritFacade() throws IOException, RestApiException {
        @SuppressWarnings("UnstableApiUsage")
        String listFilesJson = Resources.toString(Resources.getResource("json/gerrit-listfiles.json"), Charsets.UTF_8);
        JsonElement jsonElement = new JsonParser().parse(listFilesJson);
        Map<String, FileInfo> fileInfoMap = new FileInfoParser(new Gson()).parseFileInfos(jsonElement);

        Changes changes = mock(Changes.class);
        when(gerritApi.changes()).thenReturn(changes);
        ChangeApi changeApi = mock(ChangeApi.class);
        when(changes.id(CHANGE_ID)).thenReturn(changeApi);
        RevisionApi revisionApi = mock(RevisionApi.class, withSettings().lenient());
        when(changeApi.revision(REVISION_ID)).thenReturn(revisionApi);
        when(revisionApi.files()).thenReturn(fileInfoMap);
        return new GerritFacade(gerritApi, new GerritPatchset(CHANGE_ID, REVISION_ID, TAG), gerritOptions);
    }
}
