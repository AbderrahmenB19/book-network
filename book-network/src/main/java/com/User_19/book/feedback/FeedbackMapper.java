package com.User_19.book.feedback;

import com.User_19.book.book.Book;
import org.springframework.stereotype.Component;

import java.util.Objects;
@Component
public class FeedbackMapper {
    public Feedback toFeedBack(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false)
                        .shareable(false)
                        .build())

                .build();
    }

    public FeedbackResponse toFeedBackResponse(Feedback feedback, Integer userId) {
        return FeedbackResponse.builder().
                note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(),userId))
                .build();
    }
}
