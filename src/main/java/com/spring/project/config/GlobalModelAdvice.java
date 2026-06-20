package com.spring.project.config;

import com.spring.project.entity.User;
import com.spring.project.security.CustomOAuth2User;
import com.spring.project.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Inject thông tin user đang đăng nhập vào model cho MỌI request.
 * Navbar và các fragment dùng ${currentUser.fullName}, ${currentUser.avatarUrl} để hiển thị.
 *
 * Lý do dùng @ControllerAdvice thay vì sec:authentication trong template:
 * - Principal có thể là CustomUserDetails (LOCAL) hoặc CustomOAuth2User (Google)
 * - 2 class khác nhau → cú pháp sec:authentication không linh hoạt
 * - @ControllerAdvice xử lý logic Java rồi đẩy 1 object thống nhất ra model
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void addUserInfo(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return;
        }

        Object principal = authentication.getPrincipal();
        User user = null;

        if (principal instanceof CustomUserDetails) {
            user = ((CustomUserDetails) principal).getUser();
        } else if (principal instanceof CustomOAuth2User) {
            user = ((CustomOAuth2User) principal).getUser();
        }

        if (user != null) {
            model.addAttribute("currentUser", user);
        }
    }
}
