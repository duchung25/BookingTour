package com.spring.project.repository;

import com.spring.project.entity.UserAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho UserAuthProvider entity.
 * Use Case: Đăng nhập/đăng ký bằng Google OAuth, quản lý phương thức xác thực.
 */
@Repository
public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    /**
     * UC 1.1 - Đăng nhập Google: Tìm auth provider theo provider và provider_user_id (Google sub)
     */
    Optional<UserAuthProvider> findByProviderAndProviderUserId(String provider, String providerUserId);

    /**
     * UC 1.2 - Đăng nhập Google: Tìm auth provider theo provider và email
     */
    Optional<UserAuthProvider> findByProviderAndProviderEmail(String provider, String providerEmail);

    /**
     * Lấy tất cả phương thức xác thực của một user
     */
    List<UserAuthProvider> findByUserId(Long userId);

    /**
     * Kiểm tra user đã liên kết provider chưa (VD: đã liên kết Google chưa)
     */
    boolean existsByUserIdAndProvider(Long userId, String provider);

    /**
     * Tìm auth provider theo user_id và provider (VD: lấy bản ghi LOCAL của user)
     */
    Optional<UserAuthProvider> findByUserIdAndProvider(Long userId, String provider);

    /**
     * Xóa liên kết provider của user (VD: hủy liên kết Google)
     */
    void deleteByUserIdAndProvider(Long userId, String provider);
}
