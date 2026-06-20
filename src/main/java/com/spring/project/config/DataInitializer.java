package com.spring.project.config;

import com.spring.project.entity.Role;
import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.RoleRepository;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tài khoản admin mặc định:
 * Email: admin@tourbooking.com
 * Password: admin123
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdminData(RoleRepository roleRepository,
            UserRepository userRepository,
            UserAuthProviderRepository authProviderRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Đảm bảo Role ADMIN tồn tại
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role("ADMIN", "Quản trị viên hệ thống");
                        return roleRepository.save(role);
                    });

            // 2. Đảm bảo Role CUSTOMER tồn tại (cần cho hệ thống)
            if (!roleRepository.existsByName("CUSTOMER")) {
                roleRepository.save(new Role("CUSTOMER", "Khách hàng"));
            }

            // 3. Tạo tài khoản admin nếu chưa có
            String adminEmail = "admin@tourbooking.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setRole(adminRole);
                admin.setFullName("Administrator");
                admin.setEmail(adminEmail);
                admin.setStatus("ACTIVE");
                admin = userRepository.save(admin);

                // Tạo auth provider LOCAL với mật khẩu
                UserAuthProvider authProvider = new UserAuthProvider(admin, "LOCAL");
                authProvider.setPassword(passwordEncoder.encode("admin123"));
                authProvider.setEmailVerified(true);
                authProvider.setProviderEmail(adminEmail);
                authProviderRepository.save(authProvider);

                System.out.println("\n✅ Tài khoản Admin đã được tạo:");
                System.out.println("   Email:    " + adminEmail);
                System.out.println("   Password: admin123\n");
            }
        };
    }
}
