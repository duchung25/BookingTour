package com.spring.project.service;

import com.spring.project.dto.DestinationRequest;
import com.spring.project.entity.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý Điểm đến — UC Admin CRUD điểm đến.
 */
public interface DestinationService {

    Page<Destination> getDestinationList(String status, Pageable pageable);

    Destination getDestinationById(Long id);

    void createDestination(DestinationRequest request);

    void updateDestination(Long id, DestinationRequest request);

    /** Soft delete nếu còn tour dùng, hard delete nếu không. */
    void deleteDestination(Long id);

    long countTours(Long id);
}
