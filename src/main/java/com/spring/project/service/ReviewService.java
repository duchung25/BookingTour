package com.spring.project.service;

import com.spring.project.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Review createReview(Long bookingId, Long userId, int rating, String title, String content);

    boolean canReview(Long bookingId, Long userId);

    Page<Review> getReviewList(String status, Pageable pageable);

    Review getReviewById(Long id);

    void toggleStatus(Long id);

    void deleteReview(Long id);
}
