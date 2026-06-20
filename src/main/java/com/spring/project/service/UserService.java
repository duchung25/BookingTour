package com.spring.project.service;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;

/**
 * Service xử lý nghiệp vụ liên quan đến User.
 * UC 1.3: Cập nhật thông tin cá nhân + Đổi mật khẩu.
 */
public interface UserService {

    /**
     * Lấy User theo ID.
     */
    User getUserById(Long id);

    /**
     * Kiểm tra user có provider LOCAL hay không.
     * Dùng để ẩn/hiện tab "Đổi mật khẩu" trên giao diện.
     */
    boolean hasLocalProvider(Long userId);

    /**
     * Cập nhật thông tin cá nhân: fullName, phone, gender, dateOfBirth, address.
     * Trả về User đã cập nhật để refresh principal.
     */
    User updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * Đổi mật khẩu cho tài khoản LOCAL.
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * Đặt lại mật khẩu tạm thời cho tài khoản LOCAL theo email.
     */
    void resetPassword(String email, String newPassword);

    /**
     * Kiểm tra cooldown forgot-password: throw IllegalArgumentException nếu
     * email vừa request reset trong vòng 2 phút trước.
     */
    void assertCanResetPassword(String email);
}
