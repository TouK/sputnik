package pl.touk.sputnik.review.transformer;

import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface FileTransformer<T> {

    List<T> transform(List<ReviewFile> files);
}
