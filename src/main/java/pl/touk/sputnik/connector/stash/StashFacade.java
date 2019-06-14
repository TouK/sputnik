package pl.touk.sputnik.connector.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.connector.stash.json.Anchor;
import pl.touk.sputnik.connector.stash.json.Comment;
import pl.touk.sputnik.connector.stash.json.DiffSegment;
import pl.touk.sputnik.connector.stash.json.FileComment;
import pl.touk.sputnik.connector.stash.json.LineComment;
import pl.touk.sputnik.connector.stash.json.LineSegment;
import pl.touk.sputnik.connector.stash.json.ReviewElement;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StashFacade implements ConnectorFacade {
    private StashConnector stashConnector;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Configuration configuration;

    public StashFacade(@NotNull StashConnector stashConnector, Configuration configuration) {
        this.stashConnector = stashConnector;
        this.configuration = configuration;
    }

    @Override
    public Connectors name() {
        return Connectors.STASH;
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            String response = stashConnector.listFiles();
            JSONArray jsonList = JsonPath.read(response, "$.values[?(@.type != 'DELETE')].path");
            List<ReviewElement> containers = transform(jsonList, ReviewElement.class);

            List<ReviewFile> files = new ArrayList<>();
            for (ReviewElement container : containers) {
                String filePath = getFilePath(container);
                files.add(new ReviewFile(filePath));
            }
            return files;
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error when listing files", e);
        }
    }

    private String getFilePath(ReviewElement container) {
        String filePath;
        if (container.parent.isEmpty()) {
            filePath = container.name;
        } else {
            filePath = String.format("%s/%s", container.parent, container.name);
        }
        return filePath;
    }

    @Override
    public void publish(@NotNull Review review) {
        sendFileComments(review);

        try {
            String json = objectMapper.writeValueAsString(new Comment(Joiner.on(". ").join(review.getMessages())));
            stashConnector.sendReview(json);
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error while publishing review", e);
        }
    }

    private void sendFileComments(Review review) {
        boolean commentOnlyChangedLines = Boolean.parseBoolean(configuration.getProperty(GeneralOption.COMMENT_ONLY_CHANGED_LINES));

        for (ReviewFile reviewFile : review.getFiles()) {
            SingleFileChanges changes = changesForSingleFile(reviewFile.getReviewFilename());
            for (pl.touk.sputnik.review.Comment comment : reviewFile.getComments()) {
                if (noCommentExists(changes, comment)) {
                    ChangeType changeType = getChangeType(changes, comment.getLine());
                    if (changeType.equals(ChangeType.NONE) && commentOnlyChangedLines) {
                        log.info("Not posting out of context warning: {}", comment.getMessage());
                        continue;
                    }

                    try {
                        String json = objectMapper.writeValueAsString(toFileComment(reviewFile.getReviewFilename(), comment, changeType));
                        stashConnector.sendReview(json);
                    } catch (URISyntaxException | IOException e) {
                        throw new StashException("Error setting review", e);
                    }
                }

            }
        }
    }

    private boolean noCommentExists(SingleFileChanges changes, pl.touk.sputnik.review.Comment comment) {
        return !changes.containsComment(comment.getLine(), comment.getMessage());
    }

    private ChangeType getChangeType(SingleFileChanges changes, Integer line) {
        if (changes.containsChange(line)) {
            return changes.getChangeType(line);
        }
        return ChangeType.NONE;
    }

    private FileComment toFileComment(String key, pl.touk.sputnik.review.Comment comment, ChangeType changeType) {
        FileComment fileComment = new FileComment();
        fileComment.setText(comment.getMessage());
        Anchor anchor = Anchor.builder().
                path(key).
                srcPath(key).
                line(comment.getLine()).
                lineType(changeType.getNameForStash()).
                build();
        fileComment.setAnchor(anchor);
        return fileComment;
    }

    private <T> List<T> transform(JSONArray jsonList, Class<T> someClass) {
        try {
            return objectMapper.readValue(jsonList.toJSONString(), objectMapper.getTypeFactory().constructCollectionType(List.class, someClass));
        } catch (IOException e) {
            throw new StashException("Error parsing json strings to objects", e);
        }
    }

    SingleFileChanges changesForSingleFile(String filename) {
        try {
            String response = stashConnector.getDiffByLine(filename);
            JSONArray diffJsonList = JsonPath.read(response, "$.diffs[*].hunks[*].segments[*]");
            JSONArray lineCommentJsonList = JsonPath.read(response, "$.diffs[*].lineComments[*]['text', 'id']");
            List<DiffSegment> segments = transform(diffJsonList, DiffSegment.class);
            List<LineComment> lineComments = transform(lineCommentJsonList, LineComment.class);
            SingleFileChanges changes = SingleFileChanges.builder().filename(filename).build();
            for (DiffSegment segment : segments) {
                for (LineSegment line : segment.lines) {
                    changes.addChange(line.destination, ChangeType.valueOf(segment.type), getComment(lineComments, line.commentIds));
                }
            }
            return changes;
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error parsing json strings to objects", e);
        }
    }

    private List<String> getComment(List<LineComment> lineComments, List<Integer> commentIds) {
        List<String> comments = new ArrayList<>();
        if (commentIds != null) {
            for (LineComment lineComment : lineComments) {
                if (commentIds.contains(lineComment.id)) {
                    comments.add(lineComment.text);
                }
            }
        }
        return comments;
    }

    @Override
    public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {
        // all features are supported by Stash
    }

    @Override
    public void setReview(@NotNull Review review) {
        publish(review);
    }
}
