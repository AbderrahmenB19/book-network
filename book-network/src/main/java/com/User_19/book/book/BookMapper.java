package com.User_19.book.book;

import com.User_19.book.file.FileUtils;
import com.User_19.book.history.BookTransactionHistory;
import org.springframework.stereotype.Component;

import static java.nio.file.Files.getOwner;
@Component
public class BookMapper {
    public Book toBook(BookRequest bookRequest) {
        return Book.builder()
                .id(bookRequest.id())
                .title(bookRequest.title())
                .authorName(bookRequest.authorName())
                .archived(false)
                .shareable(bookRequest.shareable())

                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .authorName(book.getAuthorName())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().getFullName())

                 .cover(FileUtils.readFileFromLocation(book.getBookCover()))

                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .isbn(history.getBook().getIsbn())
                .authorName(history.getBook().getAuthorName())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnedApproved())
                .build();
    }
}
