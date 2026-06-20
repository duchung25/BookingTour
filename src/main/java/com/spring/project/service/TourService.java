package com.spring.project.service;

import com.spring.project.dto.TourCreateRequest;
import com.spring.project.dto.TourUpdateRequest;
import com.spring.project.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Service quản lý Tour — UC Admin 2.
 */
public interface TourService {

    /** UC 2.1 — Xem danh sách tour với tìm kiếm và filter */
    Page<Tour> getTourList(String keyword, String status, Pageable pageable);

    /** Lấy tour theo ID */
    Tour getTourById(Long id);

    /** UC 2.2 — Thêm tour mới (+ cascade itineraries) */
    void createTour(TourCreateRequest request);

    /** UC 2.3 — Cập nhật tour */
    void updateTour(Long id, TourUpdateRequest request);

    /** UC 2.4 — Xóa tour (soft delete → DELETED) */
    void deleteTour(Long id);

    /** UC 2.1 + 2.2 — Tìm kiếm & lọc tour cho client (khách hàng) */
    Page<Tour> searchToursForClient(String keyword, Long destinationId, Long categoryId,
                                    Integer minDuration, Integer maxDuration,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    Pageable pageable);

    /** UC 3 — Lấy chi tiết tour cho client (eager load images, category, destination, itineraries) */
    Tour getTourDetailForClient(Long id);
}
