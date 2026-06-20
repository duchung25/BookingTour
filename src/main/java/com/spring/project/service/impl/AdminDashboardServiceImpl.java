package com.spring.project.service.impl;

import com.spring.project.dto.AdminDashboardActivity;
import com.spring.project.dto.AdminDashboardMetric;
import com.spring.project.dto.AdminDashboardMonthlyRevenue;
import com.spring.project.dto.AdminDashboardRecentBooking;
import com.spring.project.dto.AdminDashboardView;
import com.spring.project.entity.Booking;
import com.spring.project.entity.Payment;
import com.spring.project.entity.Review;
import com.spring.project.entity.Tour;
import com.spring.project.repository.BookingRepository;
import com.spring.project.repository.PaymentRepository;
import com.spring.project.repository.ReviewRepository;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.repository.TourRepository;
import com.spring.project.repository.UserRepository;
import com.spring.project.service.AdminDashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final String DELETED = "DELETED";
    private static final int RECENT_LIMIT = 5;
    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final TourDepartureRepository tourDepartureRepository;

    public AdminDashboardServiceImpl(
            TourRepository tourRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            ReviewRepository reviewRepository,
            TourDepartureRepository tourDepartureRepository) {
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
        this.tourDepartureRepository = tourDepartureRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardView getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime currentMonthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = currentMonthStart.plusMonths(1);
        LocalDateTime previousMonthStart = currentMonthStart.minusMonths(1);

        long currentMonthTours = tourRepository.countByStatusNotAndCreatedAtBetween(
                DELETED, currentMonthStart, nextMonthStart);
        long previousMonthTours = tourRepository.countByStatusNotAndCreatedAtBetween(
                DELETED, previousMonthStart, currentMonthStart);

        long currentMonthBookings = bookingRepository.countByBookingStatusNotAndCreatedAtBetween(
                DELETED, currentMonthStart, nextMonthStart);
        long previousMonthBookings = bookingRepository.countByBookingStatusNotAndCreatedAtBetween(
                DELETED, previousMonthStart, currentMonthStart);

        long currentMonthCustomers = userRepository.countByRole_NameAndCreatedAtBetween(
                "CUSTOMER", currentMonthStart, nextMonthStart);
        long previousMonthCustomers = userRepository.countByRole_NameAndCreatedAtBetween(
                "CUSTOMER", previousMonthStart, currentMonthStart);

        BigDecimal currentRevenue = paymentRepository.sumSuccessfulAmountBetween(currentMonthStart, nextMonthStart);
        BigDecimal previousRevenue = paymentRepository.sumSuccessfulAmountBetween(previousMonthStart, currentMonthStart);

        return new AdminDashboardView(
                today.format(DATE_FORMATTER),
                metric(tourRepository.countByStatusNot(DELETED), currentMonthTours, previousMonthTours),
                metric(bookingRepository.countByBookingStatusNot(DELETED), currentMonthBookings, previousMonthBookings),
                metric(userRepository.countByRole_Name("CUSTOMER"), currentMonthCustomers, previousMonthCustomers),
                moneyMetric(currentRevenue, previousRevenue),
                bookingRepository.countByBookingStatus("PENDING"),
                bookingRepository.countByBookingStatus("CONFIRMED"),
                bookingRepository.countByBookingStatus("COMPLETED"),
                bookingRepository.countByBookingStatus("CANCELLED"),
                tourDepartureRepository.countByStatusAndDepartureDateGreaterThanEqual("OPEN", today),
                buildRevenueChart(currentMonthStart),
                buildRecentActivities(),
                buildRecentBookings()
        );
    }

    private AdminDashboardMetric metric(long total, long currentMonthValue, long previousMonthValue) {
        return new AdminDashboardMetric(
                formatNumber(total),
                formatChange(currentMonthValue, previousMonthValue),
                changeClass(currentMonthValue, previousMonthValue)
        );
    }

    private AdminDashboardMetric moneyMetric(BigDecimal currentMonthValue, BigDecimal previousMonthValue) {
        return new AdminDashboardMetric(
                formatMoney(currentMonthValue),
                formatChange(currentMonthValue, previousMonthValue),
                changeClass(currentMonthValue, previousMonthValue)
        );
    }

    private List<AdminDashboardMonthlyRevenue> buildRevenueChart(LocalDateTime currentMonthStart) {
        List<BigDecimal> values = new ArrayList<>();
        List<LocalDateTime> months = new ArrayList<>();
        BigDecimal max = BigDecimal.ZERO;

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = currentMonthStart.minusMonths(i);
            BigDecimal amount = paymentRepository.sumSuccessfulAmountBetween(monthStart, monthStart.plusMonths(1));
            months.add(monthStart);
            values.add(amount);
            if (amount.compareTo(max) > 0) {
                max = amount;
            }
        }

        List<AdminDashboardMonthlyRevenue> chart = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            BigDecimal amount = values.get(i);
            chart.add(new AdminDashboardMonthlyRevenue(
                    months.get(i).format(MONTH_FORMATTER),
                    formatMoney(amount),
                    calculateBarHeight(amount, max)
            ));
        }
        return chart;
    }

    private List<AdminDashboardActivity> buildRecentActivities() {
        List<AdminDashboardActivity> activities = new ArrayList<>();
        PageRequest recent = PageRequest.of(0, RECENT_LIMIT, Sort.by("createdAt").descending());

        for (Booking booking : bookingRepository.findAllWithTour(recent).getContent()) {
            activities.add(new AdminDashboardActivity(
                    "Don moi #" + booking.getBookingCode(),
                    booking.getContactName() + " dat tour " + booking.getTourDeparture().getTour().getName(),
                    "bg-primary",
                    booking.getCreatedAt()
            ));
        }

        for (Payment payment : paymentRepository.findRecentSuccessfulPayments(recent)) {
            activities.add(new AdminDashboardActivity(
                    "Thanh toan #" + payment.getBooking().getBookingCode(),
                    payment.getPaymentMethod() + " - " + formatMoney(payment.getAmount()),
                    "bg-success",
                    payment.getPaidAt() != null ? payment.getPaidAt() : payment.getCreatedAt()
            ));
        }

        for (Review review : reviewRepository.findRecentVisibleReviews(recent).getContent()) {
            activities.add(new AdminDashboardActivity(
                    "Danh gia moi",
                    review.getUser().getFullName() + " danh gia " + review.getRating() + "/5 cho " + review.getTour().getName(),
                    "bg-warning",
                    review.getCreatedAt()
            ));
        }

        for (Tour tour : tourRepository.findRecentTours(recent).getContent()) {
            activities.add(new AdminDashboardActivity(
                    "Tour moi",
                    tour.getName(),
                    "bg-info",
                    tour.getCreatedAt()
            ));
        }

        return activities.stream()
                .sorted(Comparator.comparing(
                        AdminDashboardActivity::getOccurredAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(RECENT_LIMIT)
                .toList();
    }

    private List<AdminDashboardRecentBooking> buildRecentBookings() {
        PageRequest recent = PageRequest.of(0, RECENT_LIMIT, Sort.by("createdAt").descending());
        return bookingRepository.findAllWithTour(recent)
                .map(this::toRecentBooking)
                .getContent();
    }

    private AdminDashboardRecentBooking toRecentBooking(Booking booking) {
        return new AdminDashboardRecentBooking(
                booking.getId(),
                booking.getBookingCode(),
                booking.getContactName(),
                booking.getTourDeparture().getTour().getName(),
                booking.getCreatedAt(),
                formatMoney(booking.getFinalAmount()),
                booking.getBookingStatus(),
                bookingStatusLabel(booking.getBookingStatus()),
                bookingStatusBadgeClass(booking.getBookingStatus())
        );
    }

    private int calculateBarHeight(BigDecimal amount, BigDecimal max) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || max.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        int percent = amount.multiply(BigDecimal.valueOf(100))
                .divide(max, 0, RoundingMode.HALF_UP)
                .intValue();
        return Math.max(percent, 8);
    }

    private String formatChange(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? "+100%" : "0%";
        }
        BigDecimal change = BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 1, RoundingMode.HALF_UP);
        return formatSignedPercent(change);
    }

    private String formatChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? "+100%" : "0%";
        }
        BigDecimal safeCurrent = current == null ? BigDecimal.ZERO : current;
        BigDecimal change = safeCurrent.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 1, RoundingMode.HALF_UP);
        return formatSignedPercent(change);
    }

    private String changeClass(long current, long previous) {
        return current >= previous ? "text-success" : "text-danger";
    }

    private String changeClass(BigDecimal current, BigDecimal previous) {
        BigDecimal safeCurrent = current == null ? BigDecimal.ZERO : current;
        BigDecimal safePrevious = previous == null ? BigDecimal.ZERO : previous;
        return safeCurrent.compareTo(safePrevious) >= 0 ? "text-success" : "text-danger";
    }

    private String formatSignedPercent(BigDecimal value) {
        String formatted = value.stripTrailingZeros().toPlainString() + "%";
        return value.compareTo(BigDecimal.ZERO) > 0 ? "+" + formatted : formatted;
    }

    private String formatNumber(long value) {
        return NumberFormat.getIntegerInstance(VIETNAM_LOCALE).format(value);
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        return NumberFormat.getIntegerInstance(VIETNAM_LOCALE).format(safeValue) + " VND";
    }

    private String bookingStatusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "Cho xac nhan";
            case "CONFIRMED" -> "Da xac nhan";
            case "COMPLETED" -> "Hoan thanh";
            case "CANCELLED" -> "Da huy";
            default -> status;
        };
    }

    private String bookingStatusBadgeClass(String status) {
        return switch (status) {
            case "PENDING" -> "bg-warning text-dark";
            case "CONFIRMED" -> "bg-info text-dark";
            case "COMPLETED" -> "bg-success";
            case "CANCELLED" -> "bg-danger";
            default -> "bg-secondary";
        };
    }
}
