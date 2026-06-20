package com.spring.project.security;

import com.spring.project.entity.Role;
import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.RoleRepository;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   UserAuthProviderRepository userAuthProviderRepository,
                                   RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. Gọi Google API lấy thông tin user
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. Trích xuất thông tin từ Google
        String googleSub = (String) attributes.get("sub");
        String googleEmail = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");
        String avatarUrl = (String) attributes.get("picture");
        boolean emailVerified = Boolean.TRUE.equals(attributes.get("email_verified"));

        // 3. Tìm UserAuthProvider theo Google sub ID
        Optional<UserAuthProvider> existingProvider =
                userAuthProviderRepository.findByProviderAndProviderUserId("GOOGLE", googleSub);

        User user;

        if (existingProvider.isPresent()) {
            // ===== TRƯỜNG HỢP 1: Đã từng đăng nhập Google =====
            user = existingProvider.get().getUser();

            // Kiểm tra trạng thái tài khoản (BANNED/INACTIVE không được đăng nhập)
            checkUserStatus(user);

            // Cập nhật avatar nếu Google thay đổi ảnh
            if (avatarUrl != null && !avatarUrl.equals(user.getAvatarUrl())) {
                user.setAvatarUrl(avatarUrl);
                userRepository.save(user);
            }

        } else {
            // ===== TRƯỜNG HỢP 2 & 3: Lần đầu dùng Google =====

            // Kiểm tra email đã tồn tại chưa
            Optional<User> existingUser = userRepository.findByEmail(googleEmail);

            if (existingUser.isPresent()) {
                // Email đã có → liên kết Google vào tài khoản hiện tại
                user = existingUser.get();

                // Kiểm tra trạng thái tài khoản trước khi liên kết
                checkUserStatus(user);
            } else {
                // Email hoàn toàn mới → tạo User mới
                Role customerRole = roleRepository.findByName("CUSTOMER")
                        .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại trong database"));

                user = new User();
                user.setRole(customerRole);
                user.setFullName(fullName);
                user.setEmail(googleEmail);
                user.setAvatarUrl(avatarUrl);
                user.setStatus("ACTIVE");
                user = userRepository.save(user);
            }

            // Tạo UserAuthProvider cho Google
            UserAuthProvider newProvider = new UserAuthProvider();
            newProvider.setUser(user);
            newProvider.setProvider("GOOGLE");
            newProvider.setProviderUserId(googleSub);
            newProvider.setProviderEmail(googleEmail);
            newProvider.setEmailVerified(emailVerified);
            userAuthProviderRepository.save(newProvider);
        }

        // 4. Kiểm tra trạng thái lần cuối (áp dụng cho TH3: user mới tạo luôn ACTIVE, nhưng guard phòng thủ)
        checkUserStatus(user);

        // 5. Trả về CustomOAuth2User để Spring Security lưu vào session
        return new CustomOAuth2User(user, attributes);
    }

    /**
     * Kiểm tra trạng thái tài khoản trước khi cho phép đăng nhập OAuth2.
     * Ném OAuth2AuthenticationException nếu tài khoản bị BANNED hoặc INACTIVE.
     *
     * @param user User entity cần kiểm tra
     * @throws OAuth2AuthenticationException nếu tài khoản không được phép đăng nhập
     */
    private void checkUserStatus(User user) throws OAuth2AuthenticationException {
        if ("BANNED".equals(user.getStatus())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("account_banned",
                            "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ hỗ trợ.",
                            null));
        }
        if ("INACTIVE".equals(user.getStatus())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("account_inactive",
                            "Tài khoản của bạn đã bị vô hiệu hóa.",
                            null));
        }
    }
}
