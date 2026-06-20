package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity: tour_itineraries
 * Lịch trình chi tiết từng ngày của tour.
 */
@Entity
@Table(name = "tour_itineraries")
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Min(value = 1, message = "Ngày phải bắt đầu từ 1")
    @NotNull
    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @NotBlank(message = "Tiêu đề ngày không được để trống")
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Mô tả ngày không được để trống")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 100)
    @Column(name = "meals", length = 100)
    private String meals;

    @Size(max = 150)
    @Column(name = "accommodation", length = 150)
    private String accommodation;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    // ===================== Constructors =====================

    public TourItinerary() {}

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMeals() { return meals; }
    public void setMeals(String meals) { this.meals = meals; }

    public String getAccommodation() { return accommodation; }
    public void setAccommodation(String accommodation) { this.accommodation = accommodation; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
