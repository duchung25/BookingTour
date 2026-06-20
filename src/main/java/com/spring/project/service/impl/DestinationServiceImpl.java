package com.spring.project.service.impl;

import com.spring.project.dto.DestinationRequest;
import com.spring.project.entity.Destination;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.service.DestinationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationServiceImpl(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Override
    public Page<Destination> getDestinationList(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return destinationRepository.findAllByOrderByIdDesc(pageable);
        }
        return destinationRepository.findByStatusOrderByIdDesc(status, pageable);
    }

    @Override
    public Destination getDestinationById(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Điểm đến không tồn tại"));
    }

    @Override
    @Transactional
    public void createDestination(DestinationRequest request) {
        String name = request.getName().trim();
        String country = request.getCountry().trim();
        if (destinationRepository.existsByNameAndCountry(name, country)) {
            throw new IllegalArgumentException("Điểm đến này đã tồn tại trong quốc gia đã chọn");
        }
        Destination d = new Destination();
        d.setName(name);
        d.setProvince(request.getProvince());
        d.setCountry(country);
        d.setDescription(request.getDescription());
        d.setImageUrl(request.getImageUrl());
        d.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "ACTIVE" : request.getStatus());
        destinationRepository.save(d);
    }

    @Override
    @Transactional
    public void updateDestination(Long id, DestinationRequest request) {
        Destination d = getDestinationById(id);
        String newName = request.getName().trim();
        String newCountry = request.getCountry().trim();
        boolean changedKey = !d.getName().equals(newName) || !d.getCountry().equals(newCountry);
        if (changedKey && destinationRepository.existsByNameAndCountry(newName, newCountry)) {
            throw new IllegalArgumentException("Điểm đến này đã tồn tại trong quốc gia đã chọn");
        }
        d.setName(newName);
        d.setProvince(request.getProvince());
        d.setCountry(newCountry);
        d.setDescription(request.getDescription());
        d.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            d.setStatus(request.getStatus());
        }
        destinationRepository.save(d);
    }

    @Override
    @Transactional
    public void deleteDestination(Long id) {
        Destination d = getDestinationById(id);
        long usage = destinationRepository.countToursByDestinationId(id);
        if (usage > 0) {
            d.setStatus("INACTIVE");
            destinationRepository.save(d);
        } else {
            destinationRepository.delete(d);
        }
    }

    @Override
    public long countTours(Long id) {
        return destinationRepository.countToursByDestinationId(id);
    }
}
