package com.spring.project.service;

import com.spring.project.entity.Payment;

/**
 * Service quản lý thanh toán — UC 5.
 */
public interface PaymentService {

    /** UC 5 — Tạo giao dịch thanh toán cho booking */
    Payment processPayment(Long bookingId, Long userId, String paymentMethod, String note);
}
