package com.spring.project.repository;

import com.spring.project.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Promotion entity.
 * Use Case: Quản lý khuyến mãi (Admin), áp dụng mã giảm giá (Customer).
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * UC 5 - Thanh toán: Tìm promotion theo mã code (khách nhập mã giảm giá)
     */
    Optional<Promotion> findByCode(String code);

    /**
     * Lấy khuyến mãi đang hoạt động và còn hạn (dùng khi đặt tour)
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.startDate <= :now AND p.endDate >= :now " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    /**
     * Tìm promotion hợp lệ theo mã code (còn hạn + chưa hết lượt dùng)
     */
    @Query("SELECT p FROM Promotion p WHERE p.code = :code AND p.status = 'ACTIVE' " +
           "AND p.startDate <= :now AND p.endDate >= :now " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    /**
     * Admin 4.1 - Lấy danh sách khuyến mãi theo trạng thái (list)
     */
    List<Promotion> findByStatus(String status);

    /**
     * Admin 4.1 - Lấy danh sách khuyến mãi theo trạng thái (phân trang)
     */
    Page<Promotion> findByStatus(String status, Pageable pageable);

    /**
     * Admin 4.2 - Kiểm tra mã code trùng khi tạo
     */
    boolean existsByCode(String code);

    /**
     * Admin 4.3 - Kiểm tra mã code trùng khi update (loại trừ chính mình)
     */
    boolean existsByCodeAndIdNot(String code, Long id);
}
