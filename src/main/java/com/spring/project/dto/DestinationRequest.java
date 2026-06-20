package com.spring.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO tạo/cập nhật điểm đến — UC Admin CRUD điểm đến.
 */
public class DestinationRequest {

    @NotBlank(message = "Tên điểm đến không được để trống")
    @Size(max = 150)
    private String name;

    @Size(max = 100)
    private String province;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100)
    private String country;

    private String description;

    @Size(max = 255)
    private String imageUrl;

    /** ACTIVE / INACTIVE — dùng cho update */
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
