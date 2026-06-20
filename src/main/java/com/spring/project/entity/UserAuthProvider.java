package com.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity: user_auth_providers
 * Quản lý phương thức xác thực của user (LOCAL, GOOGLE).
 * Mỗi user có thể có nhiều provider (đăng ký thường + liên kết Google).
 */
@Entity
@Table(
    name = "user_auth_providers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_provider", columnNames = {"user_id", "provider"}),
        @UniqueConstraint(name = "uq_provider_user", columnNames = {"provider", "provider_user_id"})
    }
)
public class UserAuthProvider extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Loại provider: LOCAL, GOOGLE
     */
    @NotBlank(message = "Provider không được để trống")
    @Size(max = 20)
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    /**
     * ID của user trên provider (ví dụ: Google sub ID).
     * NULL nếu provider = LOCAL.
     */
    @Size(max = 255)
    @Column(name = "provider_user_id", length = 255)
    private String providerUserId;

    /**
     * Mật khẩu đã băm.
     * Chỉ có giá trị khi provider = LOCAL. NULL nếu provider = GOOGLE.
     */
    @Size(max = 255)
    @Column(name = "password", length = 255)
    private String password;

    /**
     * Email đã được xác minh hay chưa.
     */
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    /**
     * Email từ provider (ví dụ: email Google).
     * Có thể khác với email chính trong bảng users.
     */
    @Size(max = 100)
    @Column(name = "provider_email", length = 100)
    private String providerEmail;

    // ===================== Constructors =====================

    public UserAuthProvider() {}

    public UserAuthProvider(User user, String provider) {
        this.user = user;
        this.provider = provider;
    }

    // ===================== Getters & Setters =====================

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderUserId() { return providerUserId; }
    public void setProviderUserId(String providerUserId) { this.providerUserId = providerUserId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getProviderEmail() { return providerEmail; }
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }
}
