package com.User_19.book.book;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private Integer id;
    private String authorName;
    private String isbn;
    private String owner;
    private String synopsis;
    private String title;
    private byte[] cover;
    private double rate ;
    private boolean archived;
    private boolean shareable;

}
