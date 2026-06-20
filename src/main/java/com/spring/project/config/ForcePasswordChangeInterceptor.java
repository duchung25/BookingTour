package com.spring.project.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class ForcePasswordChangeInterceptor implements HandlerInterceptor {

    private static final Set<String> ALLOWED_EXACT_PATHS = Set.of(
            "/profile",
            "/profile/change-password",
            "/logout"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) return true;
        if (session.getAttribute("justResetPasswordEmail") == null) return true;

        // Chỉ ép buộc khi user đã đăng nhập — chưa login thì để Spring Security xử lý.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (ALLOWED_EXACT_PATHS.contains(uri)) return true;

        response.sendRedirect(request.getContextPath() + "/profile?forceChange=true");
        return false;
    }
}
