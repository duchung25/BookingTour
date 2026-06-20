package com.spring.project.service;

import com.spring.project.dto.BookingCreateRequest;
import com.spring.project.dto.TravelerInput;
import com.spring.project.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service quản lý Đơn đặt tour — UC Admin 3 + UC Customer 4.1.
 */
public interface BookingService {

    // ==================== UC 4.1 — Customer: Đặt tour ====================

    /** UC 4.1 — Tạo booking mới (customer) */
    Booking createBooking(Long userId, BookingCreateRequest request);

    /** UC 6 — Xem lịch sử đặt tour (customer) */
    Page<Booking> getBookingHistory(Long userId, String status, Pageable pageable);

    /** UC 4.2 — Sửa booking (chỉ PENDING) */
    Booking updateBooking(Long bookingId, Long userId, Long departureId,
                           int adultCount, int childCount, int infantCount, String specialRequests);

    /** UC 4.3 — Hủy booking (PENDING/CONFIRMED) */
    Booking cancelBooking(Long bookingId, Long userId, String reason);

    /** Cập nhật danh sách hành khách (chỉ PENDING, count phải khớp chính xác) */
    void updateTravelers(Long bookingId, Long userId, List<TravelerInput> travelers);

    // ==================== UC Admin 3 — Quản lý đơn ====================

    /** UC 3.1 — Xem danh sách đơn đặt với tìm kiếm và filter */
    Page<Booking> getBookingList(String keyword, String status, Pageable pageable);

    /** Lấy booking theo ID */
    Booking getBookingById(Long id);

    /** UC 3.2 — Lấy danh sách trạng thái có thể chuyển */
    List<String> getAllowedTransitions(String currentStatus);

    /** UC 3.2 — Cập nhật trạng thái đơn (với pessimistic lock) */
    void updateBookingStatus(Long id, String newStatus);

    /** UC 3.3 — Xóa đơn đặt (soft delete, chỉ đơn CANCELLED) */
    void deleteBooking(Long id);
}
