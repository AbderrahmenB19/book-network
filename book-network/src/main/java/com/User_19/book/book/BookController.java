package com.User_19.book.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "book")
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest bookRequest,
            Authentication connectedUser
            ){
        return ResponseEntity.ok(bookService.save(bookRequest,connectedUser));

    }
    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(
            @PathVariable("book-id") Integer id
    ){
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    @GetMapping("/allbook")
    public ResponseEntity<List<BookResponse>> getAllbooks(){
        return ResponseEntity.ok(bookRepository.findAll().stream().map(bookMapper::toBookResponse).collect(Collectors.toList()));
    }
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBook(
            @RequestParam(name="page",defaultValue = "0",required = false) int page
            ,@RequestParam(name="size",defaultValue = "0",required = false) int size,
            Authentication ConnectedUser
    ){
        return ResponseEntity.ok(bookService.getAllbooks(page,size,ConnectedUser));
    }
    @GetMapping("/owner")
    ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name="page",defaultValue = "0",required = false) int page
            ,@RequestParam(name="size",defaultValue = "0",required = false) int size,
            Authentication ConnectedUser
    ){
        return ResponseEntity.ok(bookService.getAllbooksByOwner(page,size,ConnectedUser));
    }
    @GetMapping("/borrowed")
    ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBooksBorrowedBooks(
            @RequestParam(name="page",defaultValue = "0",required = false) int page
            ,@RequestParam(name="size",defaultValue = "0",required = false) int size,
            Authentication ConnectedUser
    ){
        return ResponseEntity.ok(bookService.getAllBooksBorrowedBooks(page,size,ConnectedUser));
    }
    @GetMapping("/returned")
    ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBooksReturnedBooks(
            @RequestParam(name="page",defaultValue = "0",required = false) int page
            ,@RequestParam(name="size",defaultValue = "0",required = false) int size,
            Authentication ConnectedUser
    ){
        return ResponseEntity.ok(bookService.getAllBooksReturnedBooks(page,size,ConnectedUser));
    }
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId,connectedUser));
    }
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId,connectedUser));
    }
    @PostMapping("borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));

    }
    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook( @PathVariable("book-id") Integer bookId,
                                                     Authentication connectedUser

    ){
        return ResponseEntity.ok(bookService.returnBorrowBook(bookId,connectedUser));

    }
    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook( @PathVariable("book-id") Integer bookId,
                                                     Authentication connectedUser

    ){
        return ResponseEntity.ok(bookService.approveReturnBorrowBook(bookId,connectedUser));

    }
    @PostMapping(value = "/cover/{book-id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser


    ){
        bookService.uploadCoverBookFile(bookId,connectedUser,file);
        return ResponseEntity.accepted().build();
    }



}
