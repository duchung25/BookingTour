package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: tour_categories
 * Danh mục tour: Du lịch biển, Du lịch núi, Văn hóa lịch sử, ...
 */
@Entity
@Table(name = "tour_categories")
public class TourCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Trạng thái danh mục: ACTIVE, INACTIVE
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    // ===================== Relationships =====================

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Tour> tours = new ArrayList<>();

    // ===================== Constructors =====================

    public TourCategory() {}

    public TourCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // ===================== Getters & Setters =====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }
}
