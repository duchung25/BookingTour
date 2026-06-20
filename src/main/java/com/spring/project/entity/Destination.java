package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: destinations
 * Điểm đến du lịch: Đà Nẵng, Hội An, Phú Quốc, ...
 */
@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên điểm đến không được để trống")
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Size(max = 100)
    @Column(name = "province", length = 100)
    private String province;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100)
    @Column(name = "country", nullable = false, length = 100)
    private String country = "Việt Nam";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /**
     * Trạng thái: ACTIVE, INACTIVE
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "destination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Tour> tours = new ArrayList<>();

    // ===================== Constructors =====================

    public Destination() {}

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }
}
