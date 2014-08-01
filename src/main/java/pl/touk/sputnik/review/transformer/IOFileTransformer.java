package pl.touk.sputnik.review.transformer;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.ReviewFile;

import java.io.File;
import java.util.List;

public class IOFileTransformer implements FileTransformer<File> {

    @Override
    @NotNull
    public List<File> transform(@NotNull List<ReviewFile> files) {
        return Lists.transform(files, new Function<ReviewFile, File>() {
            @NotNull
            @Override
            public File apply(@NotNull ReviewFile from) {
                return from.getIoFile();
            }
        });
    }
}
