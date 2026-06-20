package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entity: reviews
 * Đánh giá tour của khách hàng sau khi hoàn thành booking.
 * Mỗi booking chỉ được đánh giá 1 lần (unique constraint trên booking_id).
 */
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
    @Column(name = "rating", nullable = false)
    private int rating;

    @Size(max = 150)
    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Trạng thái hiển thị: VISIBLE, HIDDEN
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "VISIBLE";

    // ===================== Constructors =====================

    public Review() {}

    // ===================== Getters & Setters =====================

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
