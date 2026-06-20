package com.spring.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO cho nhập/cập nhật thông tin hành khách — UC 4 Booking Travelers.
 */
public class TravelerInput {

    @NotBlank(message = "Họ tên hành khách không được để trống")
    @Size(max = 100)
    private String fullName;

    private LocalDate dateOfBirth;

    @Size(max = 10)
    private String gender; // MALE, FEMALE

    @NotBlank(message = "Loại hành khách không được để trống")
    @Size(max = 20)
    private String travelerType; // ADULT, CHILD, INFANT

    @Size(max = 30)
    private String identityNumber;

    @Size(max = 50)
    private String nationality;

    @Size(max = 255)
    private String note;

    // ===================== Getters & Setters =====================

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
