package com.spring.project.repository;

import com.spring.project.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Repository cho Booking entity.
 * Use Case: Đặt tour, sửa/hủy booking, xem lịch sử, Admin quản lý đơn.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Tìm booking theo mã (dùng trong trang chi tiết booking)
     */
    Optional<Booking> findByBookingCode(String bookingCode);

    /**
     * UC 6 - Xem lịch sử đặt tour: Lấy tất cả booking của một khách hàng
     */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * UC 6 - Xem lịch sử đặt tour (phân trang)
     */
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    /**
     * UC 6 - Xem lịch sử đặt tour (phân trang, JOIN FETCH tour info)
     */
    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.tourDeparture td JOIN FETCH td.tour " +
           "WHERE b.user.id = :userId AND b.bookingStatus != 'DELETED'",
           countQuery = "SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.bookingStatus != 'DELETED'")
    Page<Booking> findByUserIdWithTour(@Param("userId") Long userId, Pageable pageable);

    /**
     * UC 6 - Lọc lịch sử theo trạng thái
     */
    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.tourDeparture td JOIN FETCH td.tour " +
           "WHERE b.user.id = :userId AND b.bookingStatus = :status",
           countQuery = "SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.bookingStatus = :status")
    Page<Booking> findByUserIdAndStatusWithTour(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);

    /**
     * Admin 3.1 - Xem danh sách đơn đặt theo trạng thái
     */
    Page<Booking> findByBookingStatus(String bookingStatus, Pageable pageable);

    /**
     * Lấy booking theo user và trạng thái
     */
    List<Booking> findByUserIdAndBookingStatus(Long userId, String bookingStatus);

    /**
     * Tìm booking theo khách hàng và tour cụ thể (để kiểm tra trước khi đánh giá)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.tourDeparture.tour.id = :tourId " +
           "AND b.bookingStatus = 'COMPLETED'")
    List<Booking> findCompletedBookingsByUserAndTour(
            @Param("userId") Long userId,
            @Param("tourId") Long tourId
    );

    /**
     * Admin 3.1 - Tìm kiếm đơn đặt theo mã hoặc tên khách
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.contactName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Booking> searchBookings(@Param("keyword") String keyword, Pageable pageable);

    /**
     * findBookingsByCustomerId - Lấy booking theo ID khách hàng
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findBookingsByCustomerId(@Param("customerId") Long customerId);

    /**
     * UC Admin 2.4 - Kiểm tra tour có đơn đặt đang active trước khi xóa
     */
    boolean existsByTourDeparture_Tour_IdAndBookingStatusIn(Long tourId, List<String> statuses);

    /**
     * UC Admin 2.5 - Kiểm tra departure có đơn đặt đang active trước khi xóa
     */
    boolean existsByTourDeparture_IdAndBookingStatusIn(Long departureId, List<String> statuses);

    // ==================== UC Admin 3 — Booking Management ====================

    /**
     * UC Admin 3.1 - Lấy tất cả booking kèm tour (JOIN FETCH tránh LazyInitializationException)
     */
    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.tourDeparture td JOIN FETCH td.tour " +
           "WHERE b.bookingStatus != 'DELETED'",
           countQuery = "SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus != 'DELETED'")
    Page<Booking> findAllWithTour(Pageable pageable);

    /**
     * UC Admin 3.1 - Lọc booking theo trạng thái kèm tour
     */
    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.tourDeparture td JOIN FETCH td.tour " +
           "WHERE b.bookingStatus = :status",
           countQuery = "SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status")
    Page<Booking> findByBookingStatusWithTour(@Param("status") String status, Pageable pageable);

    /**
     * UC Admin 3.1 - Tìm kiếm booking kèm tour
     */
    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.tourDeparture td JOIN FETCH td.tour " +
           "WHERE (LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(b.contactName) LIKE LOWER(CONCAT('%', :kw, '%')))" +
           " AND b.bookingStatus != 'DELETED'",
           countQuery = "SELECT COUNT(b) FROM Booking b " +
           "WHERE (LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(b.contactName) LIKE LOWER(CONCAT('%', :kw, '%')))" +
           " AND b.bookingStatus != 'DELETED'")
    Page<Booking> searchBookingsWithTour(@Param("kw") String keyword, Pageable pageable);

    /**
     * UC Admin 3.2 - Pessimistic lock khi cập nhật trạng thái (tránh race condition)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdWithLock(@Param("id") Long id);

    /**
     * UC Admin 4.4 - Kiểm tra promotion có booking đang active trước khi xóa
     */
    boolean existsByPromotion_IdAndBookingStatusIn(Long promotionId, List<String> statuses);

    long countByBookingStatus(String bookingStatus);

    long countByBookingStatusNot(String bookingStatus);

    long countByBookingStatusNotAndCreatedAtBetween(
            String bookingStatus,
            LocalDateTime start,
            LocalDateTime end
    );
}
