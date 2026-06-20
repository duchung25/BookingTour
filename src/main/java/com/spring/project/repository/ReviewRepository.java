package com.spring.project.repository;

import com.spring.project.entity.Review;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Review entity.
 * Use Case: Đánh giá tour (Customer UC 7), Admin xem/quản lý đánh giá.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * UC 7 - Đánh giá: Kiểm tra khách đã đánh giá cho booking này chưa
     */
    Optional<Review> findByBookingId(Long bookingId);

    /**
     * UC 3 - Xem chi tiết tour: Lấy tất cả đánh giá của một tour (còn hiện)
     */
    List<Review> findByTourIdAndStatusOrderByCreatedAtDesc(Long tourId, String status);

    /**
     * Lấy tất cả đánh giá của một tour (phân trang, dùng cho Admin)
     */
    Page<Review> findByTourId(Long tourId, Pageable pageable);

    /**
     * Lấy đánh giá của một khách hàng
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Tính điểm trung bình của tour (dùng hiển thị rating trên card tour)
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tour.id = :tourId AND r.status = 'VISIBLE'")
    Double calculateAverageRatingByTourId(@Param("tourId") Long tourId);

    /**
     * Đếm số lượng đánh giá của tour
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.tour.id = :tourId AND r.status = 'VISIBLE'")
    Long countVisibleReviewsByTourId(@Param("tourId") Long tourId);

    /**
     * Lấy đánh giá theo trạng thái (Admin ẩn/hiện đánh giá)
     */
    Page<Review> findByStatus(String status, Pageable pageable);

    @Query(value = "SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.tour " +
           "WHERE r.status = 'VISIBLE'",
           countQuery = "SELECT COUNT(r) FROM Review r WHERE r.status = 'VISIBLE'")
    Page<Review> findRecentVisibleReviews(Pageable pageable);
}
