package com.spring.project.repository;

import com.spring.project.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho Destination entity.
 * Use Case: Tìm kiếm tour theo điểm đến, lọc tour.
 */
@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    /**
     * UC 2.2 - Lọc tour: Lấy tất cả điểm đến đang hoạt động
     */
    List<Destination> findByStatus(String status);

    /**
     * UC 2.1 - Tìm điểm đến theo quốc gia
     */
    List<Destination> findByCountry(String country);

    /**
     * Tìm kiếm điểm đến theo tên (phục vụ tìm kiếm tour)
     */
    @Query("SELECT d FROM Destination d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.province) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Destination> findByDestination(@Param("keyword") String keyword);

    /**
     * Đếm số tour đang dùng điểm đến — kiểm tra trước khi xóa.
     */
    @Query("SELECT COUNT(t) FROM Tour t WHERE t.destination.id = :destinationId")
    long countToursByDestinationId(@Param("destinationId") Long destinationId);

    /**
     * Phân trang Admin.
     */
    org.springframework.data.domain.Page<Destination> findByStatusOrderByIdDesc(String status, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Destination> findAllByOrderByIdDesc(org.springframework.data.domain.Pageable pageable);

    boolean existsByNameAndCountry(String name, String country);
}
