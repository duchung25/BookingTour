package com.spring.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO cập nhật tour — UC Admin 2.3.
 * Giống TourCreateRequest nhưng thêm field status.
 */
public class TourUpdateRequest {

    @NotNull(message = "Danh mục tour không được để trống")
    private Long categoryId;

    @NotNull(message = "Điểm đến không được để trống")
    private Long destinationId;

    @NotBlank(message = "Tên tour không được để trống")
    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String slug;

    @NotBlank(message = "Điểm khởi hành không được để trống")
    @Size(max = 150)
    private String departureLocation;

    @Min(value = 1, message = "Số ngày phải ít nhất là 1")
    private int durationDays;

    @Min(value = 0, message = "Số đêm không được âm")
    private int durationNights;

    @Size(max = 100)
    private String transport;

    @Size(max = 50)
    private String hotelStandard;

    private String description;
    private String policy;
    private String includedServices;
    private String excludedServices;
    private String notes;

    /** File ảnh upload từ form (để trống nếu không thay đổi ảnh) */
    private MultipartFile imageFile;

    /** Trạng thái: ACTIVE / INACTIVE */
    private String status;

    private List<TourCreateRequest.ItineraryItem> itineraries = new ArrayList<>();

    // ===================== Getters & Setters =====================

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }

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

    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TourCreateRequest.ItineraryItem> getItineraries() { return itineraries; }
    public void setItineraries(List<TourCreateRequest.ItineraryItem> itineraries) { this.itineraries = itineraries; }
}
