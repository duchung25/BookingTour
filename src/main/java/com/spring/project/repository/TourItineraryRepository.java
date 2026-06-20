package com.spring.project.repository;

import com.spring.project.entity.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho TourItinerary entity.
 */
@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, Long> {

    /**
     * UC 3 - Xem chi tiết tour: Lấy lịch trình của tour sắp xếp theo ngày
     */
    List<TourItinerary> findByTourIdOrderByDayNumberAsc(Long tourId);

    /**
     * Xóa toàn bộ lịch trình của một tour (dùng khi cập nhật tour)
     */
    void deleteByTourId(Long tourId);
}
