package pl.touk.sputnik.engine.visitor.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.engine.diff.FileDiff;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.SputnikAssertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GerritCommentVisitorTest {
    private static final String FILENAME = "src/Cat.java";
    private static final int COMMENT_LINE_UNMODIFIED = 10;
    private static final int COMMENT_LINE_MODIFIED = 1;

    @Mock
    private GerritFileDiffBuilderWrapper gerritFileDiffBuilderWrapper;
    @Mock
    private Review review;
    @Mock
    private ReviewFile reviewFile;
    @Mock
    private Comment commentOnModifiedLine;
    @Mock
    private Comment commentOnUnmodifiedLine;

    private GerritCommentVisitor gerritCommentVisitor;

    @BeforeEach
    void setUp() {
        this.gerritCommentVisitor = new GerritCommentVisitor(gerritFileDiffBuilderWrapper);
    }

    @Test
    void shouldFilterOutComments() {
        List<FileDiff> fileDiffs = asList(mockFileDiff(FILENAME, COMMENT_LINE_MODIFIED));
        when(gerritFileDiffBuilderWrapper.buildFileDiffs()).thenReturn(fileDiffs);
        when(review.getFiles()).thenReturn(Collections.singletonList(reviewFile));
        when(reviewFile.getReviewFilename()).thenReturn(FILENAME);
        List<Comment> comments = new ArrayList<Comment>();
        comments.add(commentOnModifiedLine);
        comments.add(commentOnUnmodifiedLine);
        when(commentOnModifiedLine.getLine()).thenReturn(COMMENT_LINE_MODIFIED);
        when(commentOnUnmodifiedLine.getLine()).thenReturn(COMMENT_LINE_UNMODIFIED);
        when(reviewFile.getComments()).thenReturn(comments);

        gerritCommentVisitor.afterReview(review);

        assertThat(comments).containsExactly(commentOnModifiedLine);
    }

    @Test
    void shouldReturnIfGerritThrowsException() {
        when(gerritFileDiffBuilderWrapper.buildFileDiffs()).thenThrow(GerritException.class);

        Throwable thrown = catchThrowable(() -> gerritCommentVisitor.afterReview(review));

        assertThat(thrown).doesNotThrowAnyException();
    }

    private FileDiff mockFileDiff(String fileName, Integer... lines) {
        FileDiff fileDiff = mock(FileDiff.class);
        when(fileDiff.getFileName()).thenReturn(fileName);
        when(fileDiff.getModifiedLines()).thenReturn(Stream.of(lines).collect(Collectors.toSet()));
        return fileDiff;
    }


}