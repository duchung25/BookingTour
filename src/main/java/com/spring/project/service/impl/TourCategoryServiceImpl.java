package com.spring.project.service.impl;

import com.spring.project.dto.TourCategoryRequest;
import com.spring.project.entity.TourCategory;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.service.TourCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TourCategoryServiceImpl implements TourCategoryService {

    private final TourCategoryRepository categoryRepository;

    public TourCategoryServiceImpl(TourCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<TourCategory> getCategoryList(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return categoryRepository.findAllByOrderByIdDesc(pageable);
        }
        return categoryRepository.findByStatusOrderByIdDesc(status, pageable);
    }

    @Override
    public TourCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
    }

    @Override
    @Transactional
    public void createCategory(TourCategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        TourCategory c = new TourCategory();
        c.setName(name);
        c.setDescription(request.getDescription());
        c.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "ACTIVE" : request.getStatus());
        categoryRepository.save(c);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, TourCategoryRequest request) {
        TourCategory c = getCategoryById(id);
        String newName = request.getName().trim();
        if (!c.getName().equals(newName) && categoryRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }
        c.setName(newName);
        c.setDescription(request.getDescription());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            c.setStatus(request.getStatus());
        }
        categoryRepository.save(c);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        TourCategory c = getCategoryById(id);
        long usage = categoryRepository.countToursByCategoryId(id);
        if (usage > 0) {
            // Còn tour dùng — soft delete để giữ FK
            c.setStatus("INACTIVE");
            categoryRepository.save(c);
        } else {
            categoryRepository.delete(c);
        }
    }

    @Override
    public long countTours(Long id) {
        return categoryRepository.countToursByCategoryId(id);
    }
}
