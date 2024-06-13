package com.User_19.book.book;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int size;
    private int number;
    private long totalElement;
    private long totalPages;
    private boolean first;
    private boolean last;


}
