package com.spring.project.repository;

import com.spring.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Repository cho User entity.
 * Use Case: Đăng nhập, đăng ký, quản lý tài khoản, tìm kiếm khách hàng, khóa/mở khóa.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * UC 1.2 - Đăng nhập: Tìm user theo email
     */
    Optional<User> findByEmail(String email);

    /**
     * Tìm user theo số điện thoại
     */
    Optional<User> findByPhone(String phone);

    /**
     * Kiểm tra email đã tồn tại chưa (dùng khi đăng ký)
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra phone đã tồn tại chưa (dùng khi đăng ký)
     */
    boolean existsByPhone(String phone);

    /**
     * UC 1.3 - Cập nhật profile: Kiểm tra phone trùng nhưng loại trừ chính user đang update
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);

    /**
     * UC Admin 5.1 - Xem danh sách khách hàng theo role
     */
    List<User> findByRoleName(String roleName);

    /**
     * UC Admin 5.2 - Tìm kiếm khách hàng theo tên hoặc email
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'CUSTOMER' AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Lấy danh sách user theo trạng thái (dùng cho Khóa/Mở khóa)
     */
    List<User> findByStatus(String status);

    /**
     * UC Admin 5.3 - Lấy tất cả khách hàng (phân trang)
     */
    Page<User> findByRoleName(String roleName, Pageable pageable);

    /**
     * UC Admin 1.1 - Tìm kiếm nhân viên theo tên, email hoặc số điện thoại
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'STAFF' AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchStaff(@Param("keyword") String keyword, Pageable pageable);

    /**
     * UC Admin 1.1 - Lấy danh sách user theo role và status (phân trang)
     */
    Page<User> findByRoleNameAndStatus(String roleName, String status, Pageable pageable);

    long countByRole_Name(String roleName);

    long countByRole_NameAndCreatedAtBetween(String roleName, LocalDateTime start, LocalDateTime end);
}
