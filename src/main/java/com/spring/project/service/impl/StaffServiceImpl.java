package com.spring.project.service.impl;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.Role;
import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.RoleRepository;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.StaffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementation quản lý nhân viên (Staff) — UC Admin 1.
 * Staff = User có role.name = "STAFF". Reuse bảng users + user_auth_providers.
 */
@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(UserRepository userRepository,
                            UserAuthProviderRepository userAuthProviderRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== UC 1.1 — Xem danh sách ====================

    @Override
    public Page<User> getStaffList(String keyword, String status, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.searchStaff(keyword.trim(), pageable);
        }
        if (status != null && !status.isBlank()) {
            return userRepository.findByRoleNameAndStatus("STAFF", status, pageable);
        }
        return userRepository.findByRoleName("STAFF", pageable);
    }

    // ==================== getStaffById ====================

    @Override
    public User getStaffById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại (ID: " + id + ")"));
        if (!"STAFF".equals(user.getRole().getName())) {
            throw new IllegalArgumentException("User này không phải nhân viên");
        }
        return user;
    }

    // ==================== UC 1.2 — Thêm nhân viên ====================

    @Override
    @Transactional
    public void createStaff(RegisterRequest request) {
        // 1. Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng");
        }

        // 2. Kiểm tra phone trùng (nếu có nhập)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Số điện thoại này đã được sử dụng");
            }
        }

        // 3. Kiểm tra confirmPassword khớp
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // 4. Tìm Role STAFF
        Role staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("Role STAFF không tồn tại trong database"));

        // 5. Tạo User entity với role STAFF
        User user = new User();
        user.setRole(staffRole);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE");
        User savedUser = userRepository.save(user);

        // 6. Tạo UserAuthProvider cho LOCAL login
        UserAuthProvider authProvider = new UserAuthProvider();
        authProvider.setUser(savedUser);
        authProvider.setProvider("LOCAL");
        authProvider.setPassword(passwordEncoder.encode(request.getPassword()));
        authProvider.setEmailVerified(false);
        authProvider.setProviderEmail(request.getEmail());
        userAuthProviderRepository.save(authProvider);
    }

    // ==================== UC 1.3 — Cập nhật nhân viên ====================

    @Override
    @Transactional
    public void updateStaff(Long id, UpdateProfileRequest request) {
        User staff = getStaffById(id);

        // Kiểm tra phone trùng (loại trừ chính mình)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
                throw new IllegalArgumentException("Số điện thoại này đã được sử dụng bởi tài khoản khác");
            }
        }

        // Cập nhật các trường cho phép (KHÔNG sửa role, status, email)
        staff.setFullName(request.getFullName());
        staff.setPhone(request.getPhone());
        staff.setGender(request.getGender());
        staff.setAddress(request.getAddress());

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            staff.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        }

        userRepository.save(staff);
    }

    // ==================== UC 1.4 — Xóa (INACTIVE) nhân viên ====================

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        User staff = getStaffById(id);

        // Bảo vệ: không cho xóa chính tài khoản đang đăng nhập
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (id.equals(currentUserId)) {
            throw new IllegalArgumentException("Không thể tự vô hiệu hóa tài khoản đang đăng nhập");
        }

        // Chuyển trạng thái sang INACTIVE (nghỉ việc) — KHÔNG hard delete
        staff.setStatus("INACTIVE");
        userRepository.save(staff);
    }
}
