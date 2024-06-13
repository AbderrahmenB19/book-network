package com.User_19.book.book;

import com.User_19.book.common.BaseEntity;
import com.User_19.book.feedback.Feedback;
import com.User_19.book.history.BookTransactionHistory;
import com.User_19.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)

public class Book extends BaseEntity {

    private String title;
    private String isbn;
    private String synopsis;
    private String authorName;
    private String bookCover;
    private boolean archived;
    private boolean shareable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;
    @Transient
    public double getRate(){
        if (feedbacks==null || feedbacks.isEmpty()) return 0.0;
        var rate = this.feedbacks.stream().mapToDouble(Feedback::getNote)
                .average().orElse(0.0);
        double roundedRate =Math.round(rate*10.0)/10.0;
        return roundedRate;
    }




}
