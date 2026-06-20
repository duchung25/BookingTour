package com.spring.project.service;

/**
 * Dịch vụ gửi email qua SMTP.
 * Dùng cho: xác thực đăng ký (OTP), đặt lại mật khẩu.
 */
public interface EmailService {
    void sendVerificationOtp(String to, String otp);
    void sendNewPassword(String to, String newPassword);
}
