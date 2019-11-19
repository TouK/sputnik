package pl.touk.sputnik.engine.visitor.comment;

import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.ReviewFile;

import javax.annotation.Nonnull;

public interface CommentIncludeVisitor {

    boolean include(@Nonnull ReviewFile reviewFile, @Nonnull Comment comment);

    void init();
}
