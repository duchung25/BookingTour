package com.spring.project.dto;

import jakarta.validation.constraints.*;

/**
 * DTO nhận dữ liệu form cập nhật thông tin cá nhân.
 * Các trường được phép sửa: fullName, phone, gender, dateOfBirth, address.
 * KHÔNG cho sửa: email, role, status, avatarUrl.
 */
public class UpdateProfileRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 10, message = "Giới tính tối đa 10 ký tự")
    private String gender;

    private String dateOfBirth;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    // ===================== Constructors =====================

    public UpdateProfileRequest() {}

    // ===================== Getters & Setters =====================

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
