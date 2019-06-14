package pl.touk.sputnik.review.filter;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileExtensionFilterTest {

    @Test
    void shouldFilterOutNotAllowedExtensions() {
        List<ReviewFile> files = ImmutableList.of(createFile("one.java"), createFile("two.scala"), createFile("three.groovy"));

        List<ReviewFile> filtered = new FileExtensionFilter(ImmutableList.of("java", "groovy")).filter(files);

        assertThat(filtered).extracting("reviewFilename").containsExactly("one.java", "three.groovy");
    }

    private ReviewFile createFile(String fileName) {
        ReviewFile reviewFileMock = mock(ReviewFile.class);
        when(reviewFileMock.getReviewFilename()).thenReturn(fileName);
        return reviewFileMock;
    }

}