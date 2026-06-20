package com.spring.project.service;

import com.spring.project.dto.RegisterRequest;

public interface AuthService {

    /**
     * @param request thông tin đăng ký từ form
     * @throws IllegalArgumentException nếu email/phone trùng hoặc password không khớp
     */
    void register(RegisterRequest request);
}