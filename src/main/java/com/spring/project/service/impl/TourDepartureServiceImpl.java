package com.spring.project.service.impl;

import com.spring.project.dto.TourDepartureRequest;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourDeparture;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.repository.TourRepository;
import com.spring.project.service.TourDepartureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TourDepartureServiceImpl implements TourDepartureService {

    private final TourDepartureRepository tourDepartureRepository;
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;

    public TourDepartureServiceImpl(TourDepartureRepository tourDepartureRepository,
                                     TourRepository tourRepository,
                                     BookingRepository bookingRepository) {
        this.tourDepartureRepository = tourDepartureRepository;
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<TourDeparture> getDeparturesByTourId(Long tourId) {
        return tourDepartureRepository.findByTourIdOrderByDepartureDateAsc(tourId);
    }

    @Override
    public TourDeparture getDepartureById(Long id) {
        return tourDepartureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch khởi hành không tồn tại (ID: " + id + ")"));
    }

    @Override
    @Transactional
    public void addDeparture(TourDepartureRequest request) {
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour không tồn tại"));

        // Validate ngày
        if (!request.getDepartureDate().isBefore(request.getReturnDate())) {
            throw new IllegalArgumentException("Ngày khởi hành phải trước ngày về");
        }

        // Validate slots
        if (request.getAvailableSlots() > request.getCapacity()) {
            throw new IllegalArgumentException("Số chỗ trống không được lớn hơn sức chứa");
        }

        TourDeparture departure = new TourDeparture();
        departure.setTour(tour);
        departure.setDepartureDate(request.getDepartureDate());
        departure.setReturnDate(request.getReturnDate());
        departure.setCapacity(request.getCapacity());
        departure.setAvailableSlots(request.getAvailableSlots());
        departure.setAdultPrice(request.getAdultPrice());
        departure.setChildPrice(request.getChildPrice());
        departure.setInfantPrice(request.getInfantPrice());
        departure.setStatus("OPEN");

        tourDepartureRepository.save(departure);
    }

    @Override
    @Transactional
    public void updateDeparture(Long id, TourDepartureRequest request) {
        TourDeparture departure = getDepartureById(id);

        // Validate ngày
        if (!request.getDepartureDate().isBefore(request.getReturnDate())) {
            throw new IllegalArgumentException("Ngày khởi hành phải trước ngày về");
        }

        if (request.getAvailableSlots() > request.getCapacity()) {
            throw new IllegalArgumentException("Số chỗ trống không được lớn hơn sức chứa");
        }

        departure.setDepartureDate(request.getDepartureDate());
        departure.setReturnDate(request.getReturnDate());
        departure.setCapacity(request.getCapacity());
        departure.setAvailableSlots(request.getAvailableSlots());
        departure.setAdultPrice(request.getAdultPrice());
        departure.setChildPrice(request.getChildPrice());
        departure.setInfantPrice(request.getInfantPrice());

        tourDepartureRepository.save(departure);
    }

    @Override
    @Transactional
    public void deleteDeparture(Long id) {
        TourDeparture departure = getDepartureById(id);

        // Kiểm tra có booking đang active không
        boolean hasActiveBookings = bookingRepository
                .existsByTourDeparture_IdAndBookingStatusIn(id, List.of("PENDING", "CONFIRMED"));
        if (hasActiveBookings) {
            throw new IllegalArgumentException("Không thể xóa chuyến đang có đơn đặt PENDING/CONFIRMED");
        }

        // Hard delete
        tourDepartureRepository.delete(departure);
    }
}
