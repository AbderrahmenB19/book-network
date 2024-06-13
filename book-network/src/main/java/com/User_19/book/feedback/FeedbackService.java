package com.User_19.book.feedback;

import com.User_19.book.book.Book;
import com.User_19.book.book.BookRepository;
import com.User_19.book.book.PageResponse;
import com.User_19.book.exception.OperationNotPermittedException;
import com.User_19.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;
    public Integer saveFeedBack(FeedbackRequest request, Authentication connectedUser) {
        Book book= bookRepository.findById(request.bookId()).orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+request.bookId()));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("you can not give a feedback for an archived or not shareable book ");

        }
        User user =((User)connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("you can not give a feedback for your own book  ");


        }
        Feedback feedback=feedbackMapper.toFeedBack(request);
        return feedbackRepository.save(feedback).getId();


    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {

        User user =((User)connectedUser.getPrincipal());
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Feedback> pages =feedbackRepository.getAllfeedbackByBookId(bookId,pageable);
        List<FeedbackResponse> feedbacks=pages.stream().map(f-> feedbackMapper.toFeedBackResponse(f,user.getId())).toList();
        return PageResponse.<FeedbackResponse>builder()
                .content(feedbacks)
                .totalPages(pages.getTotalPages())
                .first(pages.isFirst())
                .last(pages.isLast())
                .number(pages.getNumber())
                .totalElement(pages.getTotalElements())
                .size(pages.getSize())
                .build();

    }
}
