package com.spring.project.controller.client;

import com.spring.project.entity.Booking;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.BookingService;
import com.spring.project.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
public class ClientReviewController {

    private final BookingService bookingService;
    private final ReviewService reviewService;

    public ClientReviewController(BookingService bookingService, ReviewService reviewService) {
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    @GetMapping("/create")
    public String reviewCreate(@RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (!reviewService.canReview(bookingId, userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể đánh giá: đơn chưa hoàn thành, không thuộc bạn, hoặc đã đánh giá rồi");
            return "redirect:/booking/history";
        }

        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "client/pages/reviewcreate";
    }

    @PostMapping("/create")
    public String reviewCreatePost(@RequestParam Long bookingId,
            @RequestParam int rating,
            @RequestParam(required = false) String title,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            reviewService.createReview(bookingId, userId, rating, title, content);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá! ⭐");
            return "redirect:/booking/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/review/create?bookingId=" + bookingId;
        }
    }
}
