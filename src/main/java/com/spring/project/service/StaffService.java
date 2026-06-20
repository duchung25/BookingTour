package com.spring.project.service;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý nhân viên (Staff) — UC Admin 1.
 * Staff = User có role.name = "STAFF".
 */
public interface StaffService {

    /**
     * UC 1.1 — Xem danh sách nhân viên với tìm kiếm và filter.
     */
    Page<User> getStaffList(String keyword, String status, Pageable pageable);

    /**
     * Lấy nhân viên theo ID, kiểm tra role = STAFF.
     */
    User getStaffById(Long id);

    /**
     * UC 1.2 — Thêm nhân viên mới (tạo User + UserAuthProvider LOCAL với role STAFF).
     */
    void createStaff(RegisterRequest request);

    /**
     * UC 1.3 — Cập nhật thông tin nhân viên (không sửa role, status, email).
     */
    void updateStaff(Long id, UpdateProfileRequest request);

    /**
     * UC 1.4 — Xóa (chuyển trạng thái INACTIVE) nhân viên.
     */
    void deleteStaff(Long id);
}
