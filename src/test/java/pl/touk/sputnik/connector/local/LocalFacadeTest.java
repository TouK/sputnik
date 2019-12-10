package pl.touk.sputnik.connector.local;

import com.google.common.collect.ImmutableList;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.Severity;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalFacadeTest {
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Repository repo;
    @Mock
    private DiffFormatter diffFormatter;
    @Mock
    private DiffEntry modifiedFile;
    @Mock
    private DiffEntry deletedFile;
    @Mock
    private DiffEntry newFile;
    @Mock
    private Review review;
    @Mock
    private LocalFacadeOutput output;

    private LocalFacade localFacade;

    @BeforeEach
    void setUp() {
        localFacade = new LocalFacade(repo, diffFormatter, output);
    }

    private void setUpDiff() throws IOException {
        when(modifiedFile.getNewPath()).thenReturn("/path/to/modifiedFile");
        when(modifiedFile.getChangeType()).thenReturn(ChangeType.MODIFY);

        when(newFile.getNewPath()).thenReturn("/path/to/newFile");
        when(newFile.getChangeType()).thenReturn(ChangeType.ADD);

        when(deletedFile.getChangeType()).thenReturn(ChangeType.DELETE);

        ObjectId head = mock(ObjectId.class);
        ObjectId headParent = mock(ObjectId.class);
        when(repo.resolve(Constants.HEAD)).thenReturn(head);
        when(repo.resolve(Constants.HEAD + "^")).thenReturn(headParent);
        when(diffFormatter.scan(headParent, head)).thenReturn(ImmutableList.of(modifiedFile, deletedFile, newFile));
    }

    @Test
    void shouldParseListFilesResponse() throws IOException {
        setUpDiff();

        List<ReviewFile> reviewFiles = localFacade.listFiles();
        assertThat(reviewFiles).isNotEmpty();
    }

    @Test
    void shouldNotListDeletedFiles() throws IOException {
        setUpDiff();

        List<ReviewFile> reviewFiles = localFacade.listFiles();
        assertThat(reviewFiles)
                .hasSize(2)
                .extracting(ReviewFile::getReviewFilename).containsExactly(modifiedFile.getNewPath(), newFile.getNewPath());
    }

    @Test
    void shouldPublishNoCommentsIfAllFilesHaveNoComments() {
        ReviewFile review1 = mock(ReviewFile.class);
        ReviewFile review2 = mock(ReviewFile.class);
        when(review.getFiles()).thenReturn(ImmutableList.of(review1, review2));

        localFacade.publish(review);

        verify(output).info("No sputnik comments");
        verify(output, never()).warn(anyString());
    }

    @Test
    void shouldWarnWithCommentsAndLineNumbers() {
        ReviewFile review1 = mock(ReviewFile.class);
        when(review1.getReviewFilename()).thenReturn("/path/to/file1");
        ReviewFile review2 = mock(ReviewFile.class);
        ReviewFile review3 = mock(ReviewFile.class);
        when(review3.getReviewFilename()).thenReturn("/path/to/file3");
        when(review1.getComments()).thenReturn(ImmutableList.of(new Comment(11, "Comment 1", Severity.INFO), new Comment(14, "Comment 2", Severity.INFO)));
        when(review3.getComments()).thenReturn(ImmutableList.of(new Comment(15, "Comment 3", Severity.INFO)));
        when(review.getMessages()).thenReturn(ImmutableList.of("message 1", "message 2"));
        when(review.getFiles()).thenReturn(ImmutableList.of(review1, review2, review3));

        localFacade.publish(review);

        verify(output, never()).info(anyString());
        InOrder order = Mockito.inOrder(output);
        order.verify(output).warn("message 1");
        order.verify(output).warn("message 2");
        order.verify(output).warn("");
        order.verify(output).warn("{} comment(s) on {}", 2, "/path/to/file1");
        order.verify(output).warn("Line {}: {}", 11, "Comment 1");
        order.verify(output).warn("Line {}: {}", 14, "Comment 2");
        order.verify(output).warn("");
        order.verify(output).warn("{} comment(s) on {}", 1, "/path/to/file3");
        order.verify(output).warn("Line {}: {}", 15, "Comment 3");
    }
}