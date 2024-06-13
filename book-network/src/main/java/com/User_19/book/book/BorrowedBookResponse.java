package com.User_19.book.book;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookResponse {
    private Integer id;
    private String authorName;
    private String isbn;

    private String title;

    private double rate ;
    private boolean returned;
    private boolean returnedApproved;

}
