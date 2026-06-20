package com.spring.project.service.impl;

import com.spring.project.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Gửi email HTML qua SMTP (Gmail).
 * Dùng MimeMessage để hỗ trợ nội dung HTML đẹp.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendVerificationOtp(String to, String otp) {
        String content = "<div style=\"font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:20px;border:1px solid #e0e0e0;border-radius:10px;\">"
                + "<div style=\"text-align:center;margin-bottom:20px;\">"
                + "<h2 style=\"color:#0d6efd;margin:0;\">✈️ Traveler</h2>"
                + "<p style=\"color:#6c757d;margin:5px 0 0 0;\">Hệ thống đặt Tour hàng đầu</p>"
                + "</div>"
                + "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin-bottom:20px;\">"
                + "<p>Chào bạn,</p>"
                + "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Traveler</strong>. Mã xác thực (OTP) của bạn là:</p>"
                + "<div style=\"text-align:center;margin:25px 0;\">"
                + "<span style=\"display:inline-block;font-size:32px;font-weight:bold;letter-spacing:5px;color:#ffc107;background:#f8f9fa;padding:10px 30px;border-radius:5px;border:1px dashed #ffc107;\">"
                + otp + "</span>"
                + "</div>"
                + "<p style=\"color:#dc3545;font-size:14px;\"><strong>Lưu ý:</strong> Mã có hiệu lực trong <strong>5 phút</strong>. Không chia sẻ mã này với bất kỳ ai.</p>"
                + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>"
                + "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin:20px 0;\">"
                + "<p style=\"font-size:12px;color:#6c757d;text-align:center;\">Email tự động từ hệ thống Traveler — không phản hồi.</p>"
                + "</div>";
        sendHtmlEmail(to, "[Traveler] Mã xác thực đăng ký tài khoản", content);
    }

    @Override
    public void sendNewPassword(String to, String newPassword) {
        String content = "<div style=\"font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:20px;border:1px solid #e0e0e0;border-radius:10px;\">"
                + "<div style=\"text-align:center;margin-bottom:20px;\">"
                + "<h2 style=\"color:#0d6efd;margin:0;\">✈️ Traveler</h2>"
                + "<p style=\"color:#6c757d;margin:5px 0 0 0;\">Hệ thống đặt Tour hàng đầu</p>"
                + "</div>"
                + "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin-bottom:20px;\">"
                + "<p>Chào bạn,</p>"
                + "<p>Mật khẩu tài khoản <strong>Traveler</strong> của bạn đã được đặt lại thành công.</p>"
                + "<p>Mật khẩu tạm thời mới:</p>"
                + "<div style=\"text-align:center;margin:25px 0;\">"
                + "<span style=\"display:inline-block;font-size:24px;font-weight:bold;color:#212529;background:#f8f9fa;padding:10px 25px;border-radius:5px;border:1px solid #ced4da;\">"
                + newPassword + "</span>"
                + "</div>"
                + "<p style=\"color:#0d6efd;font-size:14px;\"><strong>Khuyên dùng:</strong> Đăng nhập và đổi mật khẩu mới ngay trong trang cá nhân.</p>"
                + "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin:20px 0;\">"
                + "<p style=\"font-size:12px;color:#6c757d;text-align:center;\">Email tự động từ hệ thống Traveler — không phản hồi.</p>"
                + "</div>";
        sendHtmlEmail(to, "[Traveler] Đặt lại mật khẩu thành công", content);
    }
}
