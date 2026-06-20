package com.spring.project.service.impl;

import com.spring.project.dto.TourCreateRequest;
import com.spring.project.dto.TourUpdateRequest;
import com.spring.project.entity.Destination;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourCategory;
import com.spring.project.entity.TourImage;
import com.spring.project.entity.TourItinerary;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.repository.TourRepository;
import com.spring.project.service.FileStorageService;
import com.spring.project.service.TourService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;
    private final BookingRepository bookingRepository;
    private final FileStorageService fileStorageService;

    public TourServiceImpl(TourRepository tourRepository,
                           TourCategoryRepository tourCategoryRepository,
                           DestinationRepository destinationRepository,
                           BookingRepository bookingRepository,
                           FileStorageService fileStorageService) {
        this.tourRepository = tourRepository;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
        this.bookingRepository = bookingRepository;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public Page<Tour> getTourList(String keyword, String status, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return tourRepository.searchToursForAdmin(keyword.trim(), pageable);
        }
        if (status != null && !status.isBlank()) {
            return tourRepository.findByStatus(status, pageable);
        }
        return tourRepository.findAllWithDepartures(pageable);
    }


    @Override
    public Tour getTourById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour không tồn tại (ID: " + id + ")"));
    }


    @Override
    @Transactional
    public void createTour(TourCreateRequest request) {
        if (tourRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Mã tour đã tồn tại: " + request.getCode());
        }

        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? request.getSlug().trim()
                : generateSlug(request.getName());

        if (tourRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Slug đã tồn tại: " + slug);
        }

        TourCategory category = tourCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục tour không tồn tại"));
        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Điểm đến không tồn tại"));

        // 5. Tạo Tour entity
        Tour tour = new Tour();
        tour.setCategory(category);
        tour.setDestination(destination);
        tour.setCode(request.getCode().trim());
        tour.setName(request.getName().trim());
        tour.setSlug(slug);
        tour.setDepartureLocation(request.getDepartureLocation().trim());
        tour.setDurationDays(request.getDurationDays());
        tour.setDurationNights(request.getDurationNights());
        tour.setTransport(request.getTransport());
        tour.setHotelStandard(request.getHotelStandard());
        tour.setDescription(request.getDescription());
        tour.setPolicy(request.getPolicy());
        tour.setIncludedServices(request.getIncludedServices());
        tour.setExcludedServices(request.getExcludedServices());
        tour.setNotes(request.getNotes());
        tour.setStatus("ACTIVE");

        // 6. Tạo itineraries (cascade save)
        if (request.getItineraries() != null) {
            for (TourCreateRequest.ItineraryItem item : request.getItineraries()) {
                if (item.getTitle() != null && !item.getTitle().isBlank()) {
                    TourItinerary it = new TourItinerary();
                    it.setTour(tour);
                    it.setDayNumber(item.getDayNumber());
                    it.setTitle(item.getTitle().trim());
                    it.setDescription(item.getDescription() != null ? item.getDescription().trim() : "");
                    it.setSortOrder(item.getDayNumber());
                    tour.getItineraries().add(it);
                }
            }
        }

        // 7. Xử lý ảnh upload
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String fileName = fileStorageService.storeFile(request.getImageFile());
            TourImage image = new TourImage();
            image.setTour(tour);
            image.setImageUrl("/uploads/tours/" + fileName);
            image.setAltText(tour.getName());
            image.setThumbnail(true);
            image.setSortOrder(0);
            tour.getImages().add(image);
        }

        // 8. Lưu
        tourRepository.save(tour);
    }

    // ==================== UC 2.3 — Cập nhật tour ====================

    @Override
    @Transactional
    public void updateTour(Long id, TourUpdateRequest request) {
        Tour tour = getTourById(id);

        // 1. Tự sinh slug nếu để trống
        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? request.getSlug().trim()
                : generateSlug(request.getName());

        // 2. Kiểm tra slug trùng (loại trừ chính mình)
        if (tourRepository.existsBySlugAndIdNot(slug, id)) {
            throw new IllegalArgumentException("Slug đã tồn tại: " + slug);
        }

        // 3. Tìm category + destination
        TourCategory category = tourCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục tour không tồn tại"));
        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Điểm đến không tồn tại"));

        // 4. Cập nhật fields (KHÔNG sửa code)
        tour.setCategory(category);
        tour.setDestination(destination);
        tour.setName(request.getName().trim());
        tour.setSlug(slug);
        tour.setDepartureLocation(request.getDepartureLocation().trim());
        tour.setDurationDays(request.getDurationDays());
        tour.setDurationNights(request.getDurationNights());
        tour.setTransport(request.getTransport());
        tour.setHotelStandard(request.getHotelStandard());
        tour.setDescription(request.getDescription());
        tour.setPolicy(request.getPolicy());
        tour.setIncludedServices(request.getIncludedServices());
        tour.setExcludedServices(request.getExcludedServices());
        tour.setNotes(request.getNotes());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            tour.setStatus(request.getStatus());
        }

        // 5. Replace itineraries (orphanRemoval xóa cũ, thêm mới)
        tour.getItineraries().clear();
        if (request.getItineraries() != null) {
            for (TourCreateRequest.ItineraryItem item : request.getItineraries()) {
                if (item.getTitle() != null && !item.getTitle().isBlank()) {
                    TourItinerary it = new TourItinerary();
                    it.setTour(tour);
                    it.setDayNumber(item.getDayNumber());
                    it.setTitle(item.getTitle().trim());
                    it.setDescription(item.getDescription() != null ? item.getDescription().trim() : "");
                    it.setSortOrder(item.getDayNumber());
                    tour.getItineraries().add(it);
                }
            }
        }

        // 6. Xử lý ảnh upload mới (nếu có)
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            // Xóa ảnh cũ (file trên disk)
            if (!tour.getImages().isEmpty()) {
                for (TourImage oldImage : tour.getImages()) {
                    String oldUrl = oldImage.getImageUrl();
                    if (oldUrl != null && oldUrl.startsWith("/uploads/tours/")) {
                        String oldFileName = oldUrl.substring("/uploads/tours/".length());
                        fileStorageService.deleteFile(oldFileName);
                    }
                }
                tour.getImages().clear(); 
            }

            // Lưu ảnh mới
            String fileName = fileStorageService.storeFile(request.getImageFile());
            TourImage newImage = new TourImage();
            newImage.setTour(tour);
            newImage.setImageUrl("/uploads/tours/" + fileName);
            newImage.setAltText(tour.getName());
            newImage.setThumbnail(true);
            newImage.setSortOrder(0);
            tour.getImages().add(newImage);
        }

        tourRepository.save(tour);
    }

    @Override
    @Transactional
    public void deleteTour(Long id) {
        Tour tour = getTourById(id);

        boolean hasActiveBookings = bookingRepository
                .existsByTourDeparture_Tour_IdAndBookingStatusIn(id, List.of("PENDING", "CONFIRMED"));
        if (hasActiveBookings) {
            throw new IllegalArgumentException("Không thể xóa tour đang có đơn đặt PENDING/CONFIRMED. Vui lòng xử lý đơn trước.");
        }

        tour.setStatus("DELETED");
        tourRepository.save(tour);
    }

    // ==================== Helper ====================

    /**
     * Sinh slug từ tên tour: loại bỏ dấu tiếng Việt, thay khoảng trắng bằng dấu gạch nối.
     */
    private String generateSlug(String name) {
        if (name == null) return "";
        // Normalize + loại bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noDiacritics = pattern.matcher(normalized).replaceAll("");
        // Xử lý đ/Đ
        noDiacritics = noDiacritics.replace("đ", "d").replace("Đ", "D");
        // Chuyển lowercase, thay ký tự không phải chữ/số bằng dấu gạch nối
        return noDiacritics.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }


    @Override
    public Page<Tour> searchToursForClient(String keyword, Long destinationId, Long categoryId,
                                            Integer minDuration, Integer maxDuration,
                                            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                                            Pageable pageable) {
        return tourRepository.searchToursForClient(
                keyword, destinationId, categoryId, minDuration, maxDuration, minPrice, maxPrice, pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Tour getTourDetailForClient(Long id) {
        Tour tour = tourRepository.findByIdWithImagesAndCategory(id)
                .orElse(null);
        if (tour != null) {
            tour.getItineraries().size();
        }
        return tour;
    }
}
