package com.spring.project.controller.client;

import com.spring.project.dto.BookingCreateRequest;
import com.spring.project.dto.TravelerInput;
import com.spring.project.entity.Booking;
import com.spring.project.entity.Tour;
import com.spring.project.entity.User;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.BookingService;
import com.spring.project.service.TourService;
import com.spring.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class ClientBookingController {

    private final BookingService bookingService;
    private final TourService tourService;
    private final UserService userService;
    private final TourDepartureRepository tourDepartureRepository;

    public ClientBookingController(BookingService bookingService,
                                   TourService tourService,
                                   UserService userService,
                                   TourDepartureRepository tourDepartureRepository) {
        this.bookingService = bookingService;
        this.tourService = tourService;
        this.userService = userService;
        this.tourDepartureRepository = tourDepartureRepository;
    }

    @GetMapping("/create")
    public String bookingCreate(@RequestParam Long tourId,
            @RequestParam(required = false) Long departureId,
            @RequestParam(required = false) Integer adultCount,
            @RequestParam(required = false) Integer childCount,
            @RequestParam(required = false) Integer infantCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        Tour tour = tourService.getTourDetailForClient(tourId);
        if (tour == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại");
            return "redirect:/tours";
        }

        var departures = tourDepartureRepository.findAvailableDeparturesByTour(tourId, java.time.LocalDate.now());
        if (departures.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour hiện chưa có lịch khởi hành");
            return "redirect:/tours/detail?id=" + tourId;
        }

        // Pre-fill contact info from logged-in user
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        BookingCreateRequest bookingRequest = new BookingCreateRequest();
        bookingRequest.setTourId(tourId);
        bookingRequest.setContactName(user.getFullName());
        bookingRequest.setContactEmail(user.getEmail());
        bookingRequest.setContactPhone(user.getPhone() != null ? user.getPhone() : "");

        if (adultCount != null) {
            bookingRequest.setAdultCount(adultCount);
        }
        if (childCount != null) {
            bookingRequest.setChildCount(childCount);
        }
        if (infantCount != null) {
            bookingRequest.setInfantCount(infantCount);
        }

        // Pre-select departure if specified
        if (departureId != null) {
            bookingRequest.setDepartureId(departureId);
        } else {
            bookingRequest.setDepartureId(departures.get(0).getId());
        }

        model.addAttribute("tour", tour);
        model.addAttribute("departures", departures);
        model.addAttribute("bookingRequest", bookingRequest);

        return "client/pages/bookingcreate";
    }

    @PostMapping("/create")
    public String bookingCreatePost(@Valid @ModelAttribute("bookingRequest") BookingCreateRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Reload tour + departures for re-rendering form
            Tour tour = tourService.getTourDetailForClient(request.getTourId());
            var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                    request.getTourId(), java.time.LocalDate.now());
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "client/pages/bookingcreate";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Booking booking = bookingService.createBooking(userId, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt tour thành công! Mã đơn: " + booking.getBookingCode());
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            Tour tour = tourService.getTourDetailForClient(request.getTourId());
            var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                    request.getTourId(), java.time.LocalDate.now());
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            model.addAttribute("errorMessage", e.getMessage());
            return "client/pages/bookingcreate";
        }
    }

    @GetMapping("/edit")
    public String bookingEdit(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền sửa đơn này");
            return "redirect:/booking/history";
        }
        if (!"PENDING".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể sửa đơn đang Chờ xác nhận");
            return "redirect:/booking/history";
        }

        var departures = tourDepartureRepository.findAvailableDeparturesByTour(
                booking.getTourDeparture().getTour().getId(), java.time.LocalDate.now());

        model.addAttribute("booking", booking);
        model.addAttribute("departures", departures);
        return "client/pages/bookingedit";
    }

    @PostMapping("/edit")
    public String bookingEditPost(@RequestParam Long id,
            @RequestParam Long departureId,
            @RequestParam int adultCount,
            @RequestParam int childCount,
            @RequestParam int infantCount,
            @RequestParam(required = false) String specialRequests,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            bookingService.updateBooking(id, userId, departureId, adultCount, childCount, infantCount, specialRequests);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đơn đặt tour thành công!");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/edit?id=" + id;
        }
    }

    @GetMapping("/cancel")
    public String bookingCancel(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy đơn này");
            return "redirect:/booking/history";
        }

        String status = booking.getBookingStatus();
        if (!"PENDING".equals(status) && !"CONFIRMED".equals(status)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đơn ở trạng thái này");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingcancel";
    }

    @PostMapping("/cancel")
    public String bookingCancelPost(@RequestParam Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            bookingService.cancelBooking(id, userId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn đặt tour thành công!");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/history";
        }
    }

    @GetMapping("/detail")
    public String bookingDetail(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem đơn này");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingdetail";
    }

    @GetMapping("/travelers")
    public String bookingTravelers(@RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(id);

        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền cập nhật đơn này");
            return "redirect:/booking/history";
        }
        if (!"PENDING".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể cập nhật hành khách khi đơn đang Chờ xác nhận");
            return "redirect:/booking/detail?id=" + id;
        }

        model.addAttribute("booking", booking);
        return "client/pages/bookingtravelers";
    }

    @PostMapping("/travelers")
    public String bookingTravelersPost(@RequestParam Long id,
            @RequestParam("fullName") List<String> fullNames,
            @RequestParam("travelerType") List<String> travelerTypes,
            @RequestParam(value = "gender", required = false) List<String> genders,
            @RequestParam(value = "dateOfBirth", required = false) List<String> dateOfBirths,
            @RequestParam(value = "identityNumber", required = false) List<String> identityNumbers,
            @RequestParam(value = "nationality", required = false) List<String> nationalities,
            @RequestParam(value = "note", required = false) List<String> notes,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();

            List<TravelerInput> travelers = new ArrayList<>();
            for (int i = 0; i < fullNames.size(); i++) {
                TravelerInput ti = new TravelerInput();
                ti.setFullName(fullNames.get(i));
                ti.setTravelerType(travelerTypes.get(i));
                ti.setGender(genders != null && i < genders.size() ? genders.get(i) : null);
                if (dateOfBirths != null && i < dateOfBirths.size() && dateOfBirths.get(i) != null && !dateOfBirths.get(i).isBlank()) {
                    ti.setDateOfBirth(java.time.LocalDate.parse(dateOfBirths.get(i)));
                }
                ti.setIdentityNumber(identityNumbers != null && i < identityNumbers.size() ? identityNumbers.get(i) : null);
                ti.setNationality(nationalities != null && i < nationalities.size() ? nationalities.get(i) : null);
                ti.setNote(notes != null && i < notes.size() ? notes.get(i) : null);
                travelers.add(ti);
            }

            bookingService.updateTravelers(id, userId, travelers);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh sách hành khách thành công!");
            return "redirect:/booking/detail?id=" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booking/travelers?id=" + id;
        }
    }

    @GetMapping("/history")
    public String bookingHistory(@RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingService.getBookingHistory(userId, status, pageable);

        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("status", status);
        return "client/pages/bookinghistory";
    }
}
