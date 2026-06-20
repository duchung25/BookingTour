package com.spring.project.service;

import com.spring.project.dto.PromotionRequest;
import com.spring.project.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý Khuyến mãi — UC Admin 4.
 */
public interface PromotionService {

    /** UC 4.1 — Xem danh sách khuyến mãi (filter + phân trang) */
    Page<Promotion> getPromotionList(String status, Pageable pageable);

    /** Lấy promotion theo ID */
    Promotion getPromotionById(Long id);

    /** UC 4.2 — Thêm khuyến mãi */
    void createPromotion(PromotionRequest request);

    /** UC 4.3 — Cập nhật khuyến mãi */
    void updatePromotion(Long id, PromotionRequest request);

    /** UC 4.4 — Xóa khuyến mãi (soft nếu có booking, hard nếu không) */
    void deletePromotion(Long id);
}
