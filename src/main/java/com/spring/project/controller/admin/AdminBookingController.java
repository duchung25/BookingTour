package com.spring.project.controller.admin;

import com.spring.project.entity.Booking;
import com.spring.project.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String bookingList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Booking> bookingPage = bookingService.getBookingList(keyword, status, pageable);
        model.addAttribute("bookingPage", bookingPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/pages/bookinglist";
    }

    @GetMapping("/status")
    public String bookingStatusUpdate(@RequestParam Long id, Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("allowedStatuses", bookingService.getAllowedTransitions(booking.getBookingStatus()));
            return "admin/pages/bookingstatusupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/status")
    public String bookingStatusUpdatePost(@RequestParam Long id,
                                           @RequestParam String newStatus,
                                           RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/delete")
    public String bookingDelete(@RequestParam Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.getBookingById(id);
            model.addAttribute("booking", booking);
            return "admin/pages/bookingdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/delete")
    public String bookingDeletePost(@RequestParam Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn đặt thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt không tồn tại.");
        }
        return "redirect:/admin/bookings";
    }
}
