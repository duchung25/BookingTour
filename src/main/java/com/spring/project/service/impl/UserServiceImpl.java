package com.spring.project.service.impl;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserAuthProviderRepository userAuthProviderRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    @Override
    public boolean hasLocalProvider(Long userId) {
        return userAuthProviderRepository.existsByUserIdAndProvider(userId, "LOCAL");
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi tài khoản khác");
            }
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        } else {
            user.setDateOfBirth(null);
        }


        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        UserAuthProvider authProvider = userAuthProviderRepository
                .findByUserIdAndProvider(userId, "LOCAL")
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tài khoản này không có mật khẩu để đổi. Vui lòng đăng nhập bằng Google."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), authProvider.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không khớp");
        }

        authProvider.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userAuthProviderRepository.save(authProvider);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));

        UserAuthProvider authProvider = userAuthProviderRepository
                .findByUserIdAndProvider(user.getId(), "LOCAL")
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tài khoản của bạn đăng nhập bằng Google, không có mật khẩu để đặt lại."));

        authProvider.setPassword(passwordEncoder.encode(newPassword));
        userAuthProviderRepository.save(authProvider);

        user.setLastPasswordResetAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void assertCanResetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));

        LocalDateTime last = user.getLastPasswordResetAt();
        if (last == null) return;

        long secondsSince = java.time.Duration.between(last, LocalDateTime.now()).getSeconds();
        long cooldownSeconds = 120;
        if (secondsSince < cooldownSeconds) {
            long wait = cooldownSeconds - secondsSince;
            throw new IllegalArgumentException(
                    "Bạn vừa yêu cầu đặt lại mật khẩu. Vui lòng thử lại sau " + wait + " giây.");
        }
    }
}
