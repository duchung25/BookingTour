package com.spring.project.repository;

import com.spring.project.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm role theo tên (VD: "ADMIN", "CUSTOMER", "STAFF")
     */
    Optional<Role> findByName(String name);

    /**
     * Kiểm tra role đã tồn tại chưa
     */
    boolean existsByName(String name);
}
