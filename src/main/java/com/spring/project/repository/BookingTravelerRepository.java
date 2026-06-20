package com.spring.project.repository;

import com.spring.project.entity.BookingTraveler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho BookingTraveler entity.
 */
@Repository
public interface BookingTravelerRepository extends JpaRepository<BookingTraveler, Long> {

    /**
     * Lấy danh sách hành khách của một booking
     */
    List<BookingTraveler> findByBookingId(Long bookingId);

    /**
     * Lấy hành khách theo loại (ADULT, CHILD, INFANT)
     */
    List<BookingTraveler> findByBookingIdAndTravelerType(Long bookingId, String travelerType);

    /**
     * Xóa tất cả hành khách của booking (dùng khi cập nhật booking)
     */
    void deleteByBookingId(Long bookingId);
}
