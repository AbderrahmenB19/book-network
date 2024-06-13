package com.User_19.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {

    @Query(value = """
            SELECT feed
            FROM Feedback feed
            WHERE feed.book.id=:bookId
            
            """)
    Page<Feedback> getAllfeedbackByBookId(Integer bookId, Pageable pageable);
}
