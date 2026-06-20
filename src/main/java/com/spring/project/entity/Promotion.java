package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity: promotions
 * Chương trình khuyến mãi / mã giảm giá.
 */
@Entity
@Table(name = "promotions")
public class Promotion extends BaseEntity {

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Loại giảm giá: PERCENT (%), FIXED (VNĐ)
     */
    @NotBlank
    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @DecimalMin(value = "0.0", message = "Giá trị giảm không được âm")
    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount_amount", precision = 15, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_booking_amount", precision = 15, scale = 2)
    private BigDecimal minBookingAmount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Min(value = 0)
    @Column(name = "used_count", nullable = false)
    private int usedCount = 0;

    /**
     * Trạng thái: ACTIVE, INACTIVE, EXPIRED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourPromotion> tourPromotions = new ArrayList<>();

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // ===================== Constructors =====================

    public Promotion() {}

    // ===================== Getters & Setters =====================

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public BigDecimal getMinBookingAmount() { return minBookingAmount; }
    public void setMinBookingAmount(BigDecimal minBookingAmount) { this.minBookingAmount = minBookingAmount; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TourPromotion> getTourPromotions() { return tourPromotions; }
    public void setTourPromotions(List<TourPromotion> tourPromotions) { this.tourPromotions = tourPromotions; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
