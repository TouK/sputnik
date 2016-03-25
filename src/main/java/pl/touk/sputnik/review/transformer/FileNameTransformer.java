package pl.touk.sputnik.review.transformer;

import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FileNameTransformer implements FileTransformer<String> {

    @Override
    public List<String> transform(List<ReviewFile> files) {
        return files.stream()
                .map(ReviewFile::getReviewFilename)
                .collect(toList());
    }
}
