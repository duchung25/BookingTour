package com.spring.project.repository;

import com.spring.project.entity.TourPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho TourPromotion entity (bảng join tour-promotion).
 */
@Repository
public interface TourPromotionRepository extends JpaRepository<TourPromotion, Long> {

    /**
     * Lấy tất cả promotion gắn với một tour
     */
    List<TourPromotion> findByTourId(Long tourId);

    /**
     * Lấy tất cả tour gắn với một promotion
     */
    List<TourPromotion> findByPromotionId(Long promotionId);

    /**
     * Kiểm tra tour đã được gắn promotion chưa
     */
    boolean existsByTourIdAndPromotionId(Long tourId, Long promotionId);

    /**
     * Xóa liên kết giữa tour và promotion
     */
    void deleteByTourIdAndPromotionId(Long tourId, Long promotionId);
}
