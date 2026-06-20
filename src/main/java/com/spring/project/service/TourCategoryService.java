package com.spring.project.service;

import com.spring.project.dto.TourCategoryRequest;
import com.spring.project.entity.TourCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý Danh mục tour — UC Admin CRUD danh mục.
 */
public interface TourCategoryService {

    Page<TourCategory> getCategoryList(String status, Pageable pageable);

    TourCategory getCategoryById(Long id);

    void createCategory(TourCategoryRequest request);

    void updateCategory(Long id, TourCategoryRequest request);

    /** Soft delete (status=INACTIVE) nếu còn tour dùng, hard delete nếu không. */
    void deleteCategory(Long id);

    long countTours(Long id);
}
