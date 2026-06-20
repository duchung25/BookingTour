package com.spring.project.repository;

import com.spring.project.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Repository cho Tour entity.
 * Use Case: Tìm kiếm tour, lọc tour, xem danh sách tour (Customer & Admin).
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    /**
     * Tìm tour theo slug (dùng cho URL SEO-friendly)
     */
    Optional<Tour> findBySlug(String slug);

    /**
     * Tìm tour theo mã tour
     */
    Optional<Tour> findByCode(String code);

    /**
     * UC 2.2 - Lọc tour theo danh mục
     */
    Page<Tour> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

    /**
     * UC 2.2 - Lọc tour theo điểm đến
     */
    Page<Tour> findByDestinationIdAndStatus(Long destinationId, String status, Pageable pageable);

    /**
     * UC 2.1 / 2.2 - Tìm kiếm + lọc tour nâng cao theo tên hoặc điểm đến
     * Phục vụ cả Customer search và Admin quản lý tour
     */
    @Query("SELECT t FROM Tour t WHERE t.status = 'ACTIVE' AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.destination.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.departureLocation) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Tour> searchToursByNameOrDestination(@Param("keyword") String keyword, Pageable pageable);

    /**
     * UC 3 - Tìm kiếm tour còn chỗ (có chuyến khởi hành available)
     * UC 2.2 - Lọc tour còn slot trống
     */
    @Query("SELECT DISTINCT t FROM Tour t " +
           "JOIN t.departures d " +
           "WHERE t.status = 'ACTIVE' AND d.status = 'OPEN' AND d.availableSlots > 0")
    List<Tour> findAvailableTours();

    /**
     * Tìm kiếm tour phân trang dành cho Admin (không lọc status)
     */
    @Query("SELECT t FROM Tour t WHERE " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Tour> searchToursForAdmin(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy tour theo trạng thái (phân trang cho Admin)
     */
    Page<Tour> findByStatus(String status, Pageable pageable);

    /**
     * UC Admin 2.2 - Kiểm tra mã tour trùng
     */
    boolean existsByCode(String code);

    /**
     * UC Admin 2.2 - Kiểm tra slug trùng khi tạo
     */
    boolean existsBySlug(String slug);

    /**
     * UC Admin 2.3 - Kiểm tra slug trùng khi update (loại trừ chính mình)
     */
    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * UC Admin 2.1 - Filter tour theo điểm đến (phân trang)
     */
    Page<Tour> findByDestinationId(Long destinationId, Pageable pageable);

    /**
     * UC Admin 2.1 - Lấy tour kèm departures (tránh N+1 khi render giá)
     */
    @Query(value = "SELECT DISTINCT t FROM Tour t LEFT JOIN FETCH t.departures WHERE t.status != 'DELETED'",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Tour t WHERE t.status != 'DELETED'")
    Page<Tour> findAllWithDepartures(Pageable pageable);

    long countByStatusNot(String status);

    long countByStatusNotAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Tour t WHERE t.status != 'DELETED'")
    Page<Tour> findRecentTours(Pageable pageable);

    // ==================== UC 3 — Xem chi tiết tour ====================

    /**
     * UC 3 — Load tour kèm images, category, destination (eager fetch).
     * Itineraries load riêng để tránh MultipleBagFetchException.
     */
    @Query("SELECT t FROM Tour t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.destination " +
           "LEFT JOIN FETCH t.images " +
           "WHERE t.id = :id AND t.status = 'ACTIVE'")
    Optional<Tour> findByIdWithImagesAndCategory(@Param("id") Long id);

    // ==================== UC 2.1 + 2.2 — Client tìm kiếm & lọc tour ====================

    /**
     * UC 2.1 + 2.2 — Tìm kiếm & lọc tour cho client (chỉ ACTIVE, còn slot).
     * Eager fetch category, destination, thumbnail image, departure để tránh N+1.
     */
    @Query(value = "SELECT DISTINCT t FROM Tour t " +
           "LEFT JOIN FETCH t.category " +
           "LEFT JOIN FETCH t.destination " +
           "LEFT JOIN t.images img " +
           "LEFT JOIN t.departures dep " +
           "WHERE t.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(t.destination.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(t.departureLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:destinationId IS NULL OR t.destination.id = :destinationId) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:minDuration IS NULL OR t.durationDays >= :minDuration) " +
           "AND (:maxDuration IS NULL OR t.durationDays <= :maxDuration) " +
           "AND (:minPrice IS NULL AND :maxPrice IS NULL OR " +
           "     EXISTS (SELECT dep2 FROM TourDeparture dep2 WHERE dep2.tour = t " +
           "             AND (:minPrice IS NULL OR dep2.adultPrice >= :minPrice) " +
           "             AND (:maxPrice IS NULL OR dep2.adultPrice <= :maxPrice) " +
           "             AND dep2.status = 'OPEN' AND dep2.availableSlots > 0))",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Tour t WHERE t.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(t.destination.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:destinationId IS NULL OR t.destination.id = :destinationId) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:minDuration IS NULL OR t.durationDays >= :minDuration) " +
           "AND (:maxDuration IS NULL OR t.durationDays <= :maxDuration) " +
           "AND (:minPrice IS NULL AND :maxPrice IS NULL OR " +
           "     EXISTS (SELECT dep2 FROM TourDeparture dep2 WHERE dep2.tour = t " +
           "             AND (:minPrice IS NULL OR dep2.adultPrice >= :minPrice) " +
           "             AND (:maxPrice IS NULL OR dep2.adultPrice <= :maxPrice) " +
           "             AND dep2.status = 'OPEN' AND dep2.availableSlots > 0))")
    Page<Tour> searchToursForClient(
            @Param("keyword") String keyword,
            @Param("destinationId") Long destinationId,
            @Param("categoryId") Long categoryId,
            @Param("minDuration") Integer minDuration,
            @Param("maxDuration") Integer maxDuration,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
