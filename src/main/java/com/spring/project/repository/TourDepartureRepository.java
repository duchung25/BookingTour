package com.spring.project.repository;

import com.spring.project.entity.TourDeparture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho TourDeparture entity.
 * Use Case: Tìm chuyến còn slot, xem lịch khởi hành.
 */
@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Long> {

    /**
     * Lock chuyến khởi hành khi tạo booking để tránh oversell slot.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM TourDeparture d WHERE d.id = :id")
    Optional<TourDeparture> findByIdForUpdate(@Param("id") Long id);

    /**
     * Lấy tất cả chuyến của một tour
     */
    List<TourDeparture> findByTourIdOrderByDepartureDateAsc(Long tourId);

    /**
     * UC 4.1 - Đặt tour: Lấy chuyến còn slot trống của tour
     */
    @Query("SELECT d FROM TourDeparture d WHERE d.tour.id = :tourId " +
           "AND d.status = 'OPEN' AND d.availableSlots > 0 AND d.departureDate >= :today")
    List<TourDeparture> findAvailableDeparturesByTour(
            @Param("tourId") Long tourId,
            @Param("today") LocalDate today
    );

    /**
     * Lấy chuyến theo trạng thái
     */
    List<TourDeparture> findByTourIdAndStatus(Long tourId, String status);

    /**
     * Lấy chuyến trong khoảng thời gian (lọc tìm kiếm tour)
     */
    @Query("SELECT d FROM TourDeparture d WHERE d.tour.id = :tourId " +
           "AND d.departureDate BETWEEN :startDate AND :endDate")
    List<TourDeparture> findByTourIdAndDateRange(
            @Param("tourId") Long tourId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    long countByStatusAndDepartureDateGreaterThanEqual(String status, LocalDate departureDate);
}
