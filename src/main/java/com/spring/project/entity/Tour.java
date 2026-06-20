package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: tours
 * Thông tin chính của tour du lịch.
 */
@Entity
@Table(name = "tours")
public class Tour extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private TourCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @NotBlank(message = "Mã tour không được để trống")
    @Size(max = 50)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Tên tour không được để trống")
    @Size(max = 200)
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 255)
    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @NotBlank(message = "Điểm khởi hành không được để trống")
    @Size(max = 150)
    @Column(name = "departure_location", nullable = false, length = 150)
    private String departureLocation;

    @Min(value = 1, message = "Số ngày phải ít nhất là 1")
    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Min(value = 0, message = "Số đêm không được âm")
    @Column(name = "duration_nights", nullable = false)
    private int durationNights;

    @Size(max = 100)
    @Column(name = "transport", length = 100)
    private String transport;

    @Size(max = 50)
    @Column(name = "hotel_standard", length = 50)
    private String hotelStandard;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "policy", columnDefinition = "TEXT")
    private String policy;

    @Column(name = "included_services", columnDefinition = "TEXT")
    private String includedServices;

    @Column(name = "excluded_services", columnDefinition = "TEXT")
    private String excludedServices;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Trạng thái tour: ACTIVE, INACTIVE, DELETED
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayNumber ASC")
    private List<TourItinerary> itineraries = new ArrayList<>();

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourDeparture> departures = new ArrayList<>();

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourPromotion> tourPromotions = new ArrayList<>();

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    // ===================== Constructors =====================

    public Tour() {}

    // ===================== Getters & Setters =====================

    public TourCategory getCategory() { return category; }
    public void setCategory(TourCategory category) { this.category = category; }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDepartureLocation() { return departureLocation; }
    public void setDepartureLocation(String departureLocation) { this.departureLocation = departureLocation; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public int getDurationNights() { return durationNights; }
    public void setDurationNights(int durationNights) { this.durationNights = durationNights; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getHotelStandard() { return hotelStandard; }
    public void setHotelStandard(String hotelStandard) { this.hotelStandard = hotelStandard; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPolicy() { return policy; }
    public void setPolicy(String policy) { this.policy = policy; }

    public String getIncludedServices() { return includedServices; }
    public void setIncludedServices(String includedServices) { this.includedServices = includedServices; }

    public String getExcludedServices() { return excludedServices; }
    public void setExcludedServices(String excludedServices) { this.excludedServices = excludedServices; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TourImage> getImages() { return images; }
    public void setImages(List<TourImage> images) { this.images = images; }

    public List<TourItinerary> getItineraries() { return itineraries; }
    public void setItineraries(List<TourItinerary> itineraries) { this.itineraries = itineraries; }

    public List<TourDeparture> getDepartures() { return departures; }
    public void setDepartures(List<TourDeparture> departures) { this.departures = departures; }

    public List<TourPromotion> getTourPromotions() { return tourPromotions; }
    public void setTourPromotions(List<TourPromotion> tourPromotions) { this.tourPromotions = tourPromotions; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    // ===================== Computed Helpers =====================

    /**
     * Tính rating trung bình từ các review VISIBLE.
     */
    public double getAverageRating() {
        return reviews.stream()
                .filter(r -> "VISIBLE".equals(r.getStatus()))
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Đếm số review VISIBLE.
     */
    public int getVisibleReviewCount() {
        return (int) reviews.stream()
                .filter(r -> "VISIBLE".equals(r.getStatus()))
                .count();
    }
}
