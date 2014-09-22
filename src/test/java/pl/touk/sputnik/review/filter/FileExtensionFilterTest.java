package pl.touk.sputnik.review.filter;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileExtensionFilterTest {

    @Test
    public void shouldFilterNotAllowedExtensions() {
        List<ReviewFile> files = ImmutableList.of(createFile("one.java"), createFile("two.scala"), createFile("three.groovy"));

        List<ReviewFile> filtered = new FileExtensionFilter(files, ImmutableList.of("java", "groovy")).filter();

        assertThat(filtered).extracting("reviewFilename").containsExactly("one.java", "three.groovy");
    }

    private ReviewFile createFile(String fileName) {
        ReviewFile reviewFileMock = mock(ReviewFile.class);
        when(reviewFileMock.getReviewFilename()).thenReturn(fileName);
        return reviewFileMock;
    }

}