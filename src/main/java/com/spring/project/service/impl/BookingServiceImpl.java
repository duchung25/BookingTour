package com.spring.project.service.impl;

import com.spring.project.dto.BookingCreateRequest;
import com.spring.project.dto.TravelerInput;
import com.spring.project.entity.*;
import com.spring.project.repository.*;
import com.spring.project.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    /**
     * Bảng chuyển trạng thái cho phép.
     * COMPLETED và CANCELLED là final state → không có trong map.
     */
    private static final Map<String, List<String>> ALLOWED_TRANSITIONS = Map.of(
            "PENDING",   List.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", List.of("COMPLETED", "CANCELLED")
    );

    private final BookingRepository bookingRepository;
    private final TourDepartureRepository tourDepartureRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               TourDepartureRepository tourDepartureRepository,
                               UserRepository userRepository,
                               PromotionRepository promotionRepository) {
        this.bookingRepository = bookingRepository;
        this.tourDepartureRepository = tourDepartureRepository;
        this.userRepository = userRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(Long userId, BookingCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        TourDeparture departure = tourDepartureRepository.findByIdForUpdate(request.getDepartureId())
                .orElseThrow(() -> new IllegalArgumentException("Chuyến khởi hành không tồn tại"));

        if (!"OPEN".equals(departure.getStatus())) {
            throw new IllegalArgumentException("Chuyến khởi hành đã đóng hoặc bị hủy");
        }
        if (departure.getDepartureDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Chuyến khởi hành đã qua ngày");
        }

        int totalPeople = request.getAdultCount() + request.getChildCount() + request.getInfantCount();
        if (totalPeople < 1) {
            throw new IllegalArgumentException("Phải có ít nhất 1 hành khách");
        }
        if (departure.getAvailableSlots() < totalPeople) {
            throw new IllegalArgumentException(
                    "Không đủ chỗ trống. Còn lại: " + departure.getAvailableSlots() + " chỗ");
        }

        BigDecimal adultTotal = departure.getAdultPrice()
                .multiply(BigDecimal.valueOf(request.getAdultCount()));
        BigDecimal childTotal = departure.getChildPrice() != null
                ? departure.getChildPrice().multiply(BigDecimal.valueOf(request.getChildCount()))
                : BigDecimal.ZERO;
        BigDecimal infantTotal = departure.getInfantPrice() != null
                ? departure.getInfantPrice().multiply(BigDecimal.valueOf(request.getInfantCount()))
                : BigDecimal.ZERO;

        BigDecimal originalAmount = adultTotal.add(childTotal).add(infantTotal);
        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = null;

        if (request.getPromotionCode() != null && !request.getPromotionCode().isBlank()) {
            promotion = promotionRepository.findValidPromotionByCode(
                    request.getPromotionCode().trim(), LocalDateTime.now()
            ).orElse(null);

            if (promotion != null) {
                if (promotion.getMinBookingAmount() != null
                        && originalAmount.compareTo(promotion.getMinBookingAmount()) < 0) {
                    promotion = null; 
                } else {
                    if ("PERCENT".equals(promotion.getDiscountType())) {
                        discountAmount = originalAmount.multiply(promotion.getDiscountValue())
                                .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
                        if (promotion.getMaxDiscountAmount() != null
                                && discountAmount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                            discountAmount = promotion.getMaxDiscountAmount();
                        }
                    } else { 
                        discountAmount = promotion.getDiscountValue();
                    }
                }
            }
        }

        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        Booking booking = new Booking();
        booking.setBookingCode(generateBookingCode());
        booking.setUser(user);
        booking.setTourDeparture(departure);
        booking.setPromotion(promotion);
        booking.setContactName(request.getContactName());
        booking.setContactEmail(request.getContactEmail());
        booking.setContactPhone(request.getContactPhone());
        booking.setAdultCount(request.getAdultCount());
        booking.setChildCount(request.getChildCount());
        booking.setInfantCount(request.getInfantCount());
        booking.setTotalPeople(totalPeople);
        booking.setOriginalAmount(originalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setFinalAmount(finalAmount);
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setBookingStatus("PENDING");
        booking.setPaymentStatus("UNPAID");

        if (request.getTravelers() != null) {
            for (BookingCreateRequest.TravelerInfo ti : request.getTravelers()) {
                if (ti.getFullName() != null && !ti.getFullName().isBlank()) {
                    BookingTraveler traveler = new BookingTraveler();
                    traveler.setBooking(booking);
                    traveler.setFullName(ti.getFullName());
                    traveler.setGender(ti.getGender());
                    traveler.setTravelerType(ti.getTravelerType() != null ? ti.getTravelerType() : "ADULT");
                    booking.getTravelers().add(traveler);
                }
            }
        }

        departure.setAvailableSlots(departure.getAvailableSlots() - totalPeople);
        if (departure.getAvailableSlots() == 0) {
            departure.setStatus("FULL");
        }
        tourDepartureRepository.save(departure);

        if (promotion != null) {
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }

        return bookingRepository.save(booking);
    }

    private String generateBookingCode() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public Page<Booking> getBookingHistory(Long userId, String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return bookingRepository.findByUserIdAndStatusWithTour(userId, status, pageable);
        }
        return bookingRepository.findByUserIdWithTour(userId, pageable);
    }

    @Override
    @Transactional
    public Booking updateBooking(Long bookingId, Long userId, Long departureId,
                                  int adultCount, int childCount, int infantCount, String specialRequests) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn đặt tour không tồn tại"));

        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Chỉ có thể sửa đơn đang ở trạng thái Chờ xác nhận");
        }
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền sửa đơn này");
        }

        int newTotalPeople = adultCount + childCount + infantCount;
        if (newTotalPeople < 1) {
            throw new IllegalArgumentException("Phải có ít nhất 1 hành khách");
        }

        TourDeparture oldDeparture = booking.getTourDeparture();
        TourDeparture newDeparture;

        if (departureId.equals(oldDeparture.getId())) {
            newDeparture = oldDeparture;
            int effectiveSlots = newDeparture.getAvailableSlots() + booking.getTotalPeople();
            if (effectiveSlots < newTotalPeople) {
                throw new IllegalArgumentException(
                        "Không đủ chỗ trống. Còn lại: " + effectiveSlots + " chỗ");
            }
            newDeparture.setAvailableSlots(effectiveSlots - newTotalPeople);
        } else {
            oldDeparture.setAvailableSlots(oldDeparture.getAvailableSlots() + booking.getTotalPeople());
            if ("FULL".equals(oldDeparture.getStatus())) {
                oldDeparture.setStatus("OPEN");
            }
            tourDepartureRepository.save(oldDeparture);

            newDeparture = tourDepartureRepository.findById(departureId)
                    .orElseThrow(() -> new IllegalArgumentException("Chuyến khởi hành không tồn tại"));
            if (!"OPEN".equals(newDeparture.getStatus())) {
                throw new IllegalArgumentException("Chuyến khởi hành đã đóng");
            }
            if (newDeparture.getDepartureDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Chuyến khởi hành đã qua ngày");
            }
            if (newDeparture.getAvailableSlots() < newTotalPeople) {
                throw new IllegalArgumentException(
                        "Không đủ chỗ trống. Còn lại: " + newDeparture.getAvailableSlots() + " chỗ");
            }
            newDeparture.setAvailableSlots(newDeparture.getAvailableSlots() - newTotalPeople);
        }

        if (newDeparture.getAvailableSlots() == 0) {
            newDeparture.setStatus("FULL");
        }
        tourDepartureRepository.save(newDeparture);

        // Tính lại giá
        BigDecimal adultTotal = newDeparture.getAdultPrice()
                .multiply(BigDecimal.valueOf(adultCount));
        BigDecimal childTotal = newDeparture.getChildPrice() != null
                ? newDeparture.getChildPrice().multiply(BigDecimal.valueOf(childCount))
                : BigDecimal.ZERO;
        BigDecimal infantTotal = newDeparture.getInfantPrice() != null
                ? newDeparture.getInfantPrice().multiply(BigDecimal.valueOf(infantCount))
                : BigDecimal.ZERO;
        BigDecimal originalAmount = adultTotal.add(childTotal).add(infantTotal);

        // Giữ nguyên promotion nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = booking.getPromotion();
        if (promotion != null) {
            if ("PERCENT".equals(promotion.getDiscountType())) {
                discountAmount = originalAmount.multiply(promotion.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
                if (promotion.getMaxDiscountAmount() != null
                        && discountAmount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
                    discountAmount = promotion.getMaxDiscountAmount();
                }
            } else {
                discountAmount = promotion.getDiscountValue();
            }
        }

        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Cập nhật booking
        booking.setTourDeparture(newDeparture);
        booking.setAdultCount(adultCount);
        booking.setChildCount(childCount);
        booking.setInfantCount(infantCount);
        booking.setTotalPeople(newTotalPeople);
        booking.setOriginalAmount(originalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setFinalAmount(finalAmount);
        booking.setSpecialRequests(specialRequests);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId, Long userId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn đặt tour không tồn tại"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy đơn này");
        }

        String status = booking.getBookingStatus();
        if (!"PENDING".equals(status) && !"CONFIRMED".equals(status)) {
            throw new IllegalArgumentException(
                    "Chỉ có thể hủy đơn ở trạng thái Chờ xác nhận hoặc Đã xác nhận");
        }

        TourDeparture departure = booking.getTourDeparture();
        departure.setAvailableSlots(departure.getAvailableSlots() + booking.getTotalPeople());
        if ("FULL".equals(departure.getStatus())) {
            departure.setStatus("OPEN");
        }
        tourDepartureRepository.save(departure);

        booking.setBookingStatus("CANCELLED");
        if (reason != null && !reason.isBlank()) {
            String existingNotes = booking.getSpecialRequests();
            String cancelNote = "Lý do hủy: " + reason;
            booking.setSpecialRequests(existingNotes != null && !existingNotes.isBlank()
                    ? existingNotes + " | " + cancelNote : cancelNote);
        }

        return bookingRepository.save(booking);
    }

    // ==================== Cập nhật hành khách ====================

    @Override
    @Transactional
    public void updateTravelers(Long bookingId, Long userId, List<TravelerInput> travelers) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn đặt tour không tồn tại"));

        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Chỉ có thể cập nhật hành khách khi đơn đang Chờ xác nhận");
        }
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật đơn này");
        }

        // Validate tổng số khớp chính xác
        int expectedTotal = booking.getTotalPeople();
        if (travelers.size() != expectedTotal) {
            throw new IllegalArgumentException(
                    "Phải nhập đúng " + expectedTotal + " hành khách (" +
                    booking.getAdultCount() + " người lớn, " +
                    booking.getChildCount() + " trẻ em, " +
                    booking.getInfantCount() + " trẻ nhỏ)");
        }

        // Validate số lượng theo loại
        long adultInputCount = travelers.stream().filter(t -> "ADULT".equals(t.getTravelerType())).count();
        long childInputCount = travelers.stream().filter(t -> "CHILD".equals(t.getTravelerType())).count();
        long infantInputCount = travelers.stream().filter(t -> "INFANT".equals(t.getTravelerType())).count();

        if (adultInputCount != booking.getAdultCount()) {
            throw new IllegalArgumentException("Số người lớn phải là " + booking.getAdultCount());
        }
        if (childInputCount != booking.getChildCount()) {
            throw new IllegalArgumentException("Số trẻ em phải là " + booking.getChildCount());
        }
        if (infantInputCount != booking.getInfantCount()) {
            throw new IllegalArgumentException("Số trẻ nhỏ phải là " + booking.getInfantCount());
        }

        // Clear + rebuild (orphanRemoval xóa records cũ)
        booking.getTravelers().clear();

        for (TravelerInput ti : travelers) {
            if (ti.getFullName() == null || ti.getFullName().isBlank()) continue;
            BookingTraveler traveler = new BookingTraveler();
            traveler.setBooking(booking);
            traveler.setFullName(ti.getFullName().trim());
            traveler.setDateOfBirth(ti.getDateOfBirth());
            traveler.setGender(ti.getGender());
            traveler.setTravelerType(ti.getTravelerType());
            traveler.setIdentityNumber(ti.getIdentityNumber());
            traveler.setNationality(ti.getNationality());
            traveler.setNote(ti.getNote());
            booking.getTravelers().add(traveler);
        }

        bookingRepository.save(booking);
    }

    @Override
    public Page<Booking> getBookingList(String keyword, String status, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return bookingRepository.searchBookingsWithTour(keyword.trim(), pageable);
        }
        if (status != null && !status.isBlank()) {
            return bookingRepository.findByBookingStatusWithTour(status, pageable);
        }
        return bookingRepository.findAllWithTour(pageable);
    }

    // ==================== getBookingById ====================

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn đặt tour không tồn tại (ID: " + id + ")"));
    }

    // ==================== UC 3.2 — Trạng thái ====================

    @Override
    public List<String> getAllowedTransitions(String currentStatus) {
        return ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptyList());
    }

    @Override
    @Transactional
    public void updateBookingStatus(Long id, String newStatus) {
        // Pessimistic lock: tránh race condition
        Booking booking = bookingRepository.findByIdWithLock(id)
                .orElseThrow(() -> new RuntimeException("Đơn đặt tour không tồn tại (ID: " + id + ")"));

        String currentStatus = booking.getBookingStatus();

        // Validate transition
        List<String> allowed = getAllowedTransitions(currentStatus);
        if (!allowed.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Không thể chuyển từ " + currentStatus + " sang " + newStatus +
                    ". Trạng thái cho phép: " + allowed);
        }

        // Guard cho CONFIRMED: kiểm tra danh sách hành khách
        if ("CONFIRMED".equals(newStatus)) {
            if (booking.getTravelers().isEmpty()) {
                throw new IllegalArgumentException(
                        "Không thể xác nhận đơn chưa có danh sách hành khách");
            }
            if (booking.getTravelers().size() != booking.getTotalPeople()) {
                throw new IllegalArgumentException(
                        "Danh sách hành khách chưa đầy đủ (" + booking.getTravelers().size()
                        + "/" + booking.getTotalPeople() + ")");
            }
        }

        // Guard cho COMPLETED: kiểm tra đã thanh toán
        if ("COMPLETED".equals(newStatus)) {
            if (!"PAID".equals(booking.getPaymentStatus())) {
                throw new IllegalArgumentException(
                        "Không thể hoàn thành đơn chưa thanh toán đủ. Trạng thái thanh toán hiện tại: " +
                        booking.getPaymentStatus());
            }
        }

        // Guard cho CANCELLED: trả slot cho departure
        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(currentStatus)) {
            TourDeparture departure = booking.getTourDeparture();
            departure.setAvailableSlots(departure.getAvailableSlots() + booking.getTotalPeople());
            // Nếu departure đang FULL → mở lại
            if ("FULL".equals(departure.getStatus())) {
                departure.setStatus("OPEN");
            }
            tourDepartureRepository.save(departure);
        }

        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);
    }

    // ==================== UC 3.3 — Xóa đơn ====================

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);

        // Chỉ cho xóa đơn đã hủy
        if (!"CANCELLED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException(
                    "Chỉ có thể xóa đơn đã hủy. Trạng thái hiện tại: " + booking.getBookingStatus());
        }

        // Soft delete
        booking.setBookingStatus("DELETED");
        bookingRepository.save(booking);
    }
}
