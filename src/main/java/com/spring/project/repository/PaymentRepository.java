package com.spring.project.repository;

import com.spring.project.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Payment entity.
 * Use Case: Thanh toán, xem lịch sử thanh toán.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tìm giao dịch theo mã thanh toán
     */
    Optional<Payment> findByPaymentCode(String paymentCode);

    /**
     * Tìm giao dịch theo transaction ref (từ cổng thanh toán VNPAY/MOMO)
     */
    Optional<Payment> findByTransactionRef(String transactionRef);

    /**
     * Lấy tất cả giao dịch của một booking
     */
    List<Payment> findByBookingId(Long bookingId);

    /**
     * Lấy giao dịch thành công của một booking
     */
    List<Payment> findByBookingIdAndStatus(Long bookingId, String status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.status = 'SUCCESS' AND p.paidAt >= :start AND p.paidAt < :end")
    BigDecimal sumSuccessfulAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Payment p JOIN FETCH p.booking b " +
           "WHERE p.status = 'SUCCESS' ORDER BY p.createdAt DESC")
    List<Payment> findRecentSuccessfulPayments(Pageable pageable);
}
