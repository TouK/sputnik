package pl.touk.sputnik.review.filter;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface FileFilter {
    @NotNull
    List<ReviewFile> filter(@NotNull List<ReviewFile> files);
}
