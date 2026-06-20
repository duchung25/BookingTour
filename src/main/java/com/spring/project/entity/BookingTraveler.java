package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Entity: booking_travelers
 * Thông tin hành khách đi kèm trong một booking.
 */
@Entity
@Table(name = "booking_travelers")
public class BookingTraveler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotBlank(message = "Họ tên hành khách không được để trống")
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 10)
    @Column(name = "gender", length = 10)
    private String gender;

    /**
     * Loại hành khách: ADULT, CHILD, INFANT
     */
    @NotBlank
    @Column(name = "traveler_type", nullable = false, length = 20)
    private String travelerType;

    @Size(max = 30)
    @Column(name = "identity_number", length = 30)
    private String identityNumber;

    @Size(max = 50)
    @Column(name = "nationality", length = 50)
    private String nationality;

    @Size(max = 255)
    @Column(name = "note", length = 255)
    private String note;

    // ===================== Constructors =====================

    public BookingTraveler() {}

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getTravelerType() { return travelerType; }
    public void setTravelerType(String travelerType) { this.travelerType = travelerType; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
