package com.spring.project.repository;

import com.spring.project.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho TourImage entity.
 */
@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long> {

    /**
     * Lấy tất cả ảnh của một tour (sắp xếp theo thứ tự)
     */
    List<TourImage> findByTourIdOrderBySortOrderAsc(Long tourId);

    /**
     * Lấy ảnh thumbnail của tour
     */
    Optional<TourImage> findByTourIdAndThumbnailTrue(Long tourId);

    /**
     * Xóa tất cả ảnh của một tour
     */
    void deleteByTourId(Long tourId);
}
