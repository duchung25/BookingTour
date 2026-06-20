package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity: tour_departures
 * Lịch khởi hành cụ thể của tour (ngày đi, ngày về, giá, số chỗ).
 */
@Entity
@Table(name = "tour_departures")
public class TourDeparture extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @NotNull(message = "Ngày khởi hành không được để trống")
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @NotNull(message = "Ngày về không được để trống")
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Min(value = 1, message = "Sức chứa ít nhất là 1")
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Min(value = 0, message = "Số chỗ trống không được âm")
    @Column(name = "available_slots", nullable = false)
    private int availableSlots;

    @DecimalMin(value = "0.0", message = "Giá người lớn không được âm")
    @NotNull
    @Column(name = "adult_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal adultPrice;

    @DecimalMin(value = "0.0", message = "Giá trẻ em không được âm")
    @Column(name = "child_price", precision = 15, scale = 2)
    private BigDecimal childPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Giá em bé không được âm")
    @Column(name = "infant_price", precision = 15, scale = 2)
    private BigDecimal infantPrice = BigDecimal.ZERO;

    /**
     * Trạng thái chuyến: OPEN, FULL, CANCELLED, DEPARTED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "tourDeparture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // ===================== Constructors =====================

    public TourDeparture() {}

    // ===================== Getters & Setters =====================

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
