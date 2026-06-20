package com.spring.project.service.impl;

import com.spring.project.entity.Booking;
import com.spring.project.entity.Review;
import com.spring.project.entity.User;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.ReviewRepository;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean canReview(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return false;
        if (!booking.getUser().getId().equals(userId)) return false;
        if (!"COMPLETED".equals(booking.getBookingStatus())) return false;
        return reviewRepository.findByBookingId(bookingId).isEmpty();
    }

    @Override
    @Transactional
    public Review createReview(Long bookingId, Long userId, int rating, String title, String content) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn đặt tour không tồn tại"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền đánh giá đơn này");
        }

        if (!"COMPLETED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá đơn đã hoàn thành");
        }

        if (reviewRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalArgumentException("Bạn đã đánh giá cho đơn này rồi");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Review review = new Review();
        review.setBooking(booking);
        review.setUser(user);
        review.setTour(booking.getTourDeparture().getTour());
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setStatus("VISIBLE");

        return reviewRepository.save(review);
    }

    @Override
    public Page<Review> getReviewList(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return reviewRepository.findAll(pageable);
        }
        return reviewRepository.findByStatus(status, pageable);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Review review = getReviewById(id);
        review.setStatus("VISIBLE".equals(review.getStatus()) ? "HIDDEN" : "VISIBLE");
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = getReviewById(id);
        reviewRepository.delete(review);
    }
}
