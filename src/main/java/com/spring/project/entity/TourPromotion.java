package com.spring.project.entity;

import jakarta.persistence.*;

/**
 * Entity: tour_promotions
 * Bảng trung gian liên kết Tour và Promotion (Many-to-Many).
 */
@Entity
@Table(
    name = "tour_promotions",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_tour_promotions",
        columnNames = {"tour_id", "promotion_id"}
    )
)
public class TourPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    // ===================== Constructors =====================

    public TourPromotion() {}

    public TourPromotion(Tour tour, Promotion promotion) {
        this.tour = tour;
        this.promotion = promotion;
    }

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }
}
