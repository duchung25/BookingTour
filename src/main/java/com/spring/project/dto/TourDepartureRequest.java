package com.spring.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO thêm/sửa lịch khởi hành — UC Admin 2.5.
 */
public class TourDepartureRequest {

    @NotNull(message = "Tour không được để trống")
    private Long tourId;

    @NotNull(message = "Ngày khởi hành không được để trống")
    private LocalDate departureDate;

    @NotNull(message = "Ngày về không được để trống")
    private LocalDate returnDate;

    @Min(value = 1, message = "Sức chứa ít nhất là 1")
    private int capacity;

    @Min(value = 0, message = "Số chỗ trống không được âm")
    private int availableSlots;

    @NotNull(message = "Giá người lớn không được để trống")
    @DecimalMin(value = "0.0", message = "Giá người lớn không được âm")
    private BigDecimal adultPrice;

    @DecimalMin(value = "0.0", message = "Giá trẻ em không được âm")
    private BigDecimal childPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Giá em bé không được âm")
    private BigDecimal infantPrice = BigDecimal.ZERO;

    // ===================== Getters & Setters =====================

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }

    public BigDecimal getAdultPrice() { return adultPrice; }
    public void setAdultPrice(BigDecimal adultPrice) { this.adultPrice = adultPrice; }

    public BigDecimal getChildPrice() { return childPrice; }
    public void setChildPrice(BigDecimal childPrice) { this.childPrice = childPrice; }

    public BigDecimal getInfantPrice() { return infantPrice; }
    public void setInfantPrice(BigDecimal infantPrice) { this.infantPrice = infantPrice; }
}
