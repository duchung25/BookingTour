package com.spring.project.service.impl;

import com.spring.project.entity.Booking;
import com.spring.project.entity.Payment;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.PaymentRepository;
import com.spring.project.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation thanh toán — UC 5.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Payment processPayment(Long bookingId, Long userId, String paymentMethod, String note) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn đặt tour không tồn tại"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền thanh toán đơn này");
        }
        if ("CANCELLED".equals(booking.getBookingStatus()) || "DELETED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Không thể thanh toán đơn đã hủy");
        }
        if ("PAID".equals(booking.getPaymentStatus())) {
            throw new IllegalArgumentException("Đơn đặt tour đã được thanh toán");
        }
        Payment payment = new Payment();
        payment.setPaymentCode("PM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setBooking(booking);
        payment.setAmount(booking.getFinalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentType("FULL");
        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        payment.setNote(note);

        paymentRepository.save(payment);
        booking.setPaymentStatus("PAID");
        bookingRepository.save(booking);

        return payment;
    }
}
