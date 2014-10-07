package pl.touk.sputnik.review.transformer;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public class FileNameTransformer implements FileTransformer<String> {

    @Override
    public List<String> transform(List<ReviewFile> files) {
        return Lists.transform(files, new Function<ReviewFile, String>() {
            @NotNull
            @Override
            public String apply(ReviewFile from) {
                return from.getReviewFilename();
            }
        });
    }
}
