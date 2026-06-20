package com.spring.project.security;

import com.spring.project.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Không tìm thấy thông tin đăng nhập");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getUserId();
        }

        throw new RuntimeException("Không xác định được user đang đăng nhập");
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Không tìm thấy thông tin đăng nhập");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getUser();
        }

        throw new RuntimeException("Không xác định được user đang đăng nhập");
    }

    /**
     * Đồng bộ lại principal trong SecurityContextHolder sau khi update profile.
     * Phải dùng đúng loại Authentication token:
     * - LOCAL login → UsernamePasswordAuthenticationToken
     * - Google login → OAuth2AuthenticationToken (cần giữ registrationId)
     */
    public static void refreshAuthentication(User updatedUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails oldPrincipal) {
            // LOCAL login → tạo lại UsernamePasswordAuthenticationToken
            CustomUserDetails newPrincipal = new CustomUserDetails(updatedUser, oldPrincipal.getPassword());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    newPrincipal, newPrincipal.getPassword(), newPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

        } else if (principal instanceof CustomOAuth2User oldPrincipal) {
            // Google login → tạo lại OAuth2AuthenticationToken (giữ registrationId)
            CustomOAuth2User newPrincipal = new CustomOAuth2User(updatedUser, oldPrincipal.getAttributes());
            String registrationId = ((OAuth2AuthenticationToken) auth).getAuthorizedClientRegistrationId();
            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    newPrincipal, newPrincipal.getAuthorities(), registrationId);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
