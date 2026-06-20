package com.spring.project.repository;

import com.spring.project.entity.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho TourCategory entity.
 */
@Repository
public interface TourCategoryRepository extends JpaRepository<TourCategory, Long> {

    /**
     * Tìm danh mục theo tên
     */
    Optional<TourCategory> findByName(String name);

    /**
     * Lấy danh mục theo trạng thái (ACTIVE)
     */
    List<TourCategory> findByStatus(String status);

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     */
    boolean existsByName(String name);

    /**
     * Đếm số tour đang dùng danh mục — kiểm tra trước khi xóa.
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(t) FROM Tour t WHERE t.category.id = :categoryId")
    long countToursByCategoryId(@org.springframework.data.repository.query.Param("categoryId") Long categoryId);

    /**
     * Phân trang danh sách danh mục (Admin).
     */
    org.springframework.data.domain.Page<TourCategory> findByStatusOrderByIdDesc(String status, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<TourCategory> findAllByOrderByIdDesc(org.springframework.data.domain.Pageable pageable);
}
