package pl.touk.sputnik.review.filter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class FileExtensionFilter implements FileFilter {
    private final List<String> allowedExtensions;

    @NotNull
    public List<ReviewFile> filter(@NotNull List<ReviewFile> files) {
        log.info("Filtering out review files with allowed extensions {}", allowedExtensions);
        List<ReviewFile> filtered = new ArrayList<>();

        for (ReviewFile file : files) {
            String extension = StringUtils.substringAfterLast(file.getReviewFilename(), ".");
            if (allowedExtensions.contains(extension)) {
                filtered.add(file);
            } else {
                log.info("File {} was filtered out due to not allowed extension {}", file.getReviewFilename(), extension);
            }
        }

        log.info("Total {} of {} files had allowed extensions {}", filtered.size(), files.size(), allowedExtensions);
        return filtered;
    }
}
