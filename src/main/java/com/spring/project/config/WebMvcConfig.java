package com.spring.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cấu hình thêm Resource Handler để serve file upload từ thư mục local.
 * URL /uploads/** sẽ trỏ tới thư mục gốc "uploads/" trên disk.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/tours}")
    private String uploadDir;

    private final ForcePasswordChangeInterceptor forcePasswordChangeInterceptor;

    public WebMvcConfig(ForcePasswordChangeInterceptor forcePasswordChangeInterceptor) {
        this.forcePasswordChangeInterceptor = forcePasswordChangeInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().getParent();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(forcePasswordChangeInterceptor)
                .excludePathPatterns(
                        "/css/**", "/js/**", "/images/**", "/fonts/**",
                        "/assets/**", "/uploads/**", "/error/**",
                        "/login", "/admin/login", "/logout"
                );
    }
}
