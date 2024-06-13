package com.User_19.book.book;

import com.User_19.book.exception.OperationNotPermittedException;
import com.User_19.book.file.FileStorageService;
import com.User_19.book.history.BookTransactionHistory;
import com.User_19.book.history.BookTransactionHistoryRepo;
import com.User_19.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepo transactionnalHistoryReo;
    private final FileStorageService fileStorageService;
    public Integer save(BookRequest bookRequest, Authentication connectedUser) {
        User user=((User) connectedUser.getPrincipal());
        Book book= bookMapper.toBook(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book).getId();

    }

    public BookResponse getBookById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book found with this Id"+ id));
    }

    public PageResponse<BookResponse> getAllbooks(int page, int size, Authentication connectedUser) {
        User user= ((User)connectedUser.getPrincipal());
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Book> books= bookRepository.findAllDisplayedBooks(pageable,user.getId());
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        log.info("ssssssssssssss",bookResponses);
        return new PageResponse<>(
                bookResponses,
                books.getSize(),
                books.getNumber(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );

    }

    public PageResponse<BookResponse> getAllbooksByOwner(int page, int size, Authentication connectedUser) {
        User user= ((User)connectedUser.getPrincipal());
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Book> books= bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getSize(),
                books.getNumber(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );


    }

    public PageResponse<BorrowedBookResponse> getAllBooksBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user=((User) connectedUser.getPrincipal());
        Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<BookTransactionHistory> allBorrowedBook= transactionnalHistoryReo.findAllBorrowedBooks(pageable,user.getId());
        List<BorrowedBookResponse> bookResponses=allBorrowedBook.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBook.getSize(),
                allBorrowedBook.getNumber(),
                allBorrowedBook.getTotalElements(),
                allBorrowedBook.getTotalPages(),
                allBorrowedBook.isFirst(),
                allBorrowedBook.isLast()

        );
    }

    public PageResponse<BorrowedBookResponse> getAllBooksReturnedBooks(int page, int size, Authentication connectedUser) {
        User user=((User) connectedUser.getPrincipal());
        Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<BookTransactionHistory> allBorrowedBook= transactionnalHistoryReo.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedBookResponse> bookResponses=allBorrowedBook.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBook.getSize(),
                allBorrowedBook.getNumber(),
                allBorrowedBook.getTotalElements(),
                allBorrowedBook.getTotalPages(),
                allBorrowedBook.isFirst(),
                allBorrowedBook.isLast()

        );


    }

    public Integer updateShareableStatus(Integer bookId,Authentication connectedUser) {
        Book book= bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("no book has this ID:"+bookId));
        User user= ((User)connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;

    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book= bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("no book has this ID:"+bookId));
        User user= ((User)connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book= bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("no book has this ID:"+bookId));
        User user= ((User)connectedUser.getPrincipal());
        if(book.isArchived()||!book.isShareable()){
            throw  new OperationNotPermittedException("the request book cannot be borrowed since it is archived or not shareable ");
        }
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borrow ur won book");

        }
        final boolean isAlreadyBorrowed= transactionnalHistoryReo.isAlreadyBorrowedByUser(bookId,user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("the request book is already is borrowed");

        }
        BookTransactionHistory bookTransactionHistory= BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnedApproved(false)

                .build();
        return transactionnalHistoryReo.save(bookTransactionHistory).getId();


    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("no book has this ID:"+bookId));
        User user=((User)connectedUser.getPrincipal());

        if(book.isArchived()||!book.isShareable()){
                throw  new OperationNotPermittedException("the request book cannot be borrowed since it is archived or not shareable ");
            }
            if(Objects.equals(book.getOwner().getId(),user.getId())){
                throw new OperationNotPermittedException("You cannot borrow ur won book");

            }
            BookTransactionHistory bookTransactionHistory= transactionnalHistoryReo.findByBookIdAndUserId(bookId,user.getId()).orElseThrow(()-> new OperationNotPermittedException("u did not borrow this book"));
            bookTransactionHistory.setReturned(true);
            return transactionnalHistoryReo.save(bookTransactionHistory).getId();


    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("no book has this ID:"+bookId));
        if(book.isArchived()||!book.isShareable()){
            throw  new OperationNotPermittedException("the request book cannot be borrowed since it is archived or not shareable ");
        }
        User user= ((User)connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");

        }
        BookTransactionHistory bookTransactionHistory= transactionnalHistoryReo.findByBookIdAndOwnerId(bookId,user.getId()).orElseThrow(()-> new OperationNotPermittedException("the book is not returned yet"));
        bookTransactionHistory.setReturnedApproved(true);
        return transactionnalHistoryReo.save(bookTransactionHistory).getId();



    }

    public void uploadCoverBookFile(Integer bookId, Authentication connectedUser, MultipartFile file) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("no book has this ID:"+bookId));
        User user= ((User)connectedUser.getPrincipal());
        var bookCover=fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
        
                                            
