package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity: bookings
 * Đơn đặt tour của khách hàng.
 */
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @NotBlank(message = "Mã đặt tour không được để trống")
    @Size(max = 50)
    @Column(name = "booking_code", nullable = false, unique = true, length = 50)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_departure_id", nullable = false)
    private TourDeparture tourDeparture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @NotBlank
    @Size(max = 100)
    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    @NotBlank
    @Size(max = 20)
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Min(value = 1, message = "Số người lớn ít nhất là 1")
    @Column(name = "adult_count", nullable = false)
    private int adultCount = 1;

    @Min(value = 0)
    @Column(name = "child_count", nullable = false)
    private int childCount = 0;

    @Min(value = 0)
    @Column(name = "infant_count", nullable = false)
    private int infantCount = 0;

    @Min(value = 1)
    @Column(name = "total_people", nullable = false)
    private int totalPeople;

    @DecimalMin(value = "0.0")
    @NotNull
    @Column(name = "original_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal originalAmount;

    @DecimalMin(value = "0.0")
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0")
    @NotNull
    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    /**
     * Trạng thái đặt tour: PENDING, CONFIRMED, CANCELLED, COMPLETED
     */
    @Column(name = "booking_status", nullable = false, length = 20)
    private String bookingStatus = "PENDING";

    /**
     * Trạng thái thanh toán: UNPAID, PARTIAL, PAID, REFUNDED
     */
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "UNPAID";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingTraveler> travelers = new ArrayList<>();

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Review review;

    // ===================== Constructors =====================

    public Booking() {}

    // ===================== Getters & Setters =====================

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TourDeparture getTourDeparture() { return tourDeparture; }
    public void setTourDeparture(TourDeparture tourDeparture) { this.tourDeparture = tourDeparture; }

    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public int getAdultCount() { return adultCount; }
    public void setAdultCount(int adultCount) { this.adultCount = adultCount; }

    public int getChildCount() { return childCount; }
    public void setChildCount(int childCount) { this.childCount = childCount; }

    public int getInfantCount() { return infantCount; }
    public void setInfantCount(int infantCount) { this.infantCount = infantCount; }

    public int getTotalPeople() { return totalPeople; }
    public void setTotalPeople(int totalPeople) { this.totalPeople = totalPeople; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public List<BookingTraveler> getTravelers() { return travelers; }
    public void setTravelers(List<BookingTraveler> travelers) { this.travelers = travelers; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
}
