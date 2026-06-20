package com.spring.project.dto;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho UC 4.1 — Đặt tour (Create Booking).
 */
public class BookingCreateRequest {

    @NotNull(message = "Tour ID không được để trống")
    private Long tourId;

    @NotNull(message = "Chuyến khởi hành không được để trống")
    private Long departureId;

    @NotBlank(message = "Họ tên liên hệ không được để trống")
    @Size(max = 100)
    private String contactName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String contactEmail;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20)
    private String contactPhone;

    @Min(value = 1, message = "Số người lớn ít nhất là 1")
    private int adultCount = 1;

    @Min(value = 0)
    private int childCount = 0;

    @Min(value = 0)
    private int infantCount = 0;

    private String specialRequests;

    private String promotionCode;

    private List<TravelerInfo> travelers = new ArrayList<>();

    // ===================== Inner class =====================

    public static class TravelerInfo {
        @NotBlank(message = "Họ tên hành khách không được để trống")
        private String fullName;
        private String gender;
        private String travelerType; // ADULT, CHILD, INFANT

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getTravelerType() { return travelerType; }
        public void setTravelerType(String travelerType) { this.travelerType = travelerType; }
    }

    // ===================== Getters & Setters =====================

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public Long getDepartureId() { return departureId; }
    public void setDepartureId(Long departureId) { this.departureId = departureId; }

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

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getPromotionCode() { return promotionCode; }
    public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }

    public List<TravelerInfo> getTravelers() { return travelers; }
    public void setTravelers(List<TravelerInfo> travelers) { this.travelers = travelers; }
}
