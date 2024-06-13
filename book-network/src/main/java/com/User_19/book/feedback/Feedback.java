package com.User_19.book.feedback;

import com.User_19.book.book.Book;
import com.User_19.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

public class Feedback extends BaseEntity {

    private Double note;     //number of Stars
    private String comment ;
    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;


}
