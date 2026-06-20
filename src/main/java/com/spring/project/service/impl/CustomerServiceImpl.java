package com.spring.project.service.impl;

import com.spring.project.entity.User;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;

    public CustomerServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> getCustomerList(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.searchCustomers(keyword.trim(), pageable);
        }
        return userRepository.findByRoleName("CUSTOMER", pageable);
    }


    @Override
    public User getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng (ID: " + id + ")"));

        if (!"CUSTOMER".equals(user.getRole().getName())) {
            throw new IllegalArgumentException("User này không phải khách hàng");
        }
        return user;
    }


    @Override
    @Transactional
    public void toggleCustomerStatus(Long id) {
        User customer = getCustomerById(id);

        if ("ACTIVE".equals(customer.getStatus())) {
            customer.setStatus("BANNED");
        } else if ("BANNED".equals(customer.getStatus())) {
            customer.setStatus("ACTIVE");
        } else {
            throw new IllegalArgumentException(
                    "Không thể thay đổi trạng thái. Trạng thái hiện tại: " + customer.getStatus());
        }

        userRepository.save(customer);
    }
}
