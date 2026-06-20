package com.spring.project.service;

import com.spring.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý Khách hàng — UC Admin 5.
 */
public interface CustomerService {

    /** UC 5.1 + 5.2 — Xem danh sách + Tìm kiếm khách hàng */
    Page<User> getCustomerList(String keyword, Pageable pageable);

    /** Lấy khách hàng theo ID (chỉ CUSTOMER role) */
    User getCustomerById(Long id);

    /** UC 5.3 — Toggle trạng thái: ACTIVE ↔ BANNED */
    void toggleCustomerStatus(Long id);
}
