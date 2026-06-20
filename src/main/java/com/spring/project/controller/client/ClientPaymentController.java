package com.spring.project.controller.client;

import com.spring.project.entity.Booking;
import com.spring.project.entity.Payment;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.BookingService;
import com.spring.project.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class ClientPaymentController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    public ClientPaymentController(BookingService bookingService, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public String payment(@RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();
        Booking booking = bookingService.getBookingById(bookingId);

        // Kiểm tra booking thuộc user
        if (!booking.getUser().getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền truy cập đơn này");
            return "redirect:/booking/history";
        }

        // Kiểm tra đã thanh toán
        if ("PAID".equals(booking.getPaymentStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn đặt tour đã được thanh toán");
            return "redirect:/booking/history";
        }

        // Kiểm tra đơn hủy
        if ("CANCELLED".equals(booking.getBookingStatus()) || "DELETED".equals(booking.getBookingStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thanh toán đơn đã hủy");
            return "redirect:/booking/history";
        }

        model.addAttribute("booking", booking);
        return "client/pages/payment";
    }

    @PostMapping
    public String paymentProcess(@RequestParam Long bookingId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Payment payment = paymentService.processPayment(bookingId, userId, paymentMethod, note);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thanh toán thành công! Mã giao dịch: " + payment.getPaymentCode());
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/payment?bookingId=" + bookingId;
        }
    }
}
