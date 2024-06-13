package com.User_19.book.feedback;

import com.User_19.book.book.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbaack")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {
    private final FeedbackService service;
    @PostMapping
    public ResponseEntity<Integer> saveFeedBack(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.saveFeedBack(request, connectedUser));

    }
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "page",defaultValue = "0",required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId,page,size,connectedUser));
    }

}

