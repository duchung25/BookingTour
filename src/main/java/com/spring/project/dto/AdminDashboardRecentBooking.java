package com.spring.project.dto;

import java.time.LocalDateTime;

public class AdminDashboardRecentBooking {

    private final Long id;
    private final String bookingCode;
    private final String contactName;
    private final String tourName;
    private final LocalDateTime createdAt;
    private final String finalAmountText;
    private final String bookingStatus;
    private final String statusLabel;
    private final String statusBadgeClass;

    public AdminDashboardRecentBooking(
            Long id,
            String bookingCode,
            String contactName,
            String tourName,
            LocalDateTime createdAt,
            String finalAmountText,
            String bookingStatus,
            String statusLabel,
            String statusBadgeClass) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.contactName = contactName;
        this.tourName = tourName;
        this.createdAt = createdAt;
        this.finalAmountText = finalAmountText;
        this.bookingStatus = bookingStatus;
        this.statusLabel = statusLabel;
        this.statusBadgeClass = statusBadgeClass;
    }

    public Long getId() {
        return id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public String getContactName() {
        return contactName;
    }

    public String getTourName() {
        return tourName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getFinalAmountText() {
        return finalAmountText;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getStatusBadgeClass() {
        return statusBadgeClass;
    }
}
