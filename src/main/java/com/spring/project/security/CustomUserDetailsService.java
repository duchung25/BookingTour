package com.spring.project.security;

import com.spring.project.entity.User;
import com.spring.project.entity.UserAuthProvider;
import com.spring.project.repository.UserAuthProviderRepository;
import com.spring.project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserAuthProviderRepository userAuthProviderRepository) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));

        if ("BANNED".equals(user.getStatus())) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }
        UserAuthProvider authProvider = userAuthProviderRepository
                .findByUserIdAndProvider(user.getId(), "LOCAL")
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Tài khoản chưa đặt mật khẩu. Vui lòng đăng nhập bằng Google."));

        return new CustomUserDetails(user, authProvider.getPassword());
    }
}
