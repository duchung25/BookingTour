package com.spring.project.controller.admin;

import com.spring.project.entity.Review;
import com.spring.project.service.ReviewService;
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
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String reviewList(@RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewService.getReviewList(status, pageable);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("status", status);
        return "admin/pages/reviewlist";
    }

    @PostMapping("/toggle")
    public String reviewToggle(@RequestParam Long id,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái đánh giá.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/reviews?page=" + page + (status != null ? "&status=" + status : "");
    }

    @GetMapping("/delete")
    public String reviewDelete(@RequestParam Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("review", reviewService.getReviewById(id));
            return "admin/pages/reviewdelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/reviews";
        }
    }

    @PostMapping("/delete")
    public String reviewDeletePost(@RequestParam Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đánh giá thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/reviews";
    }
}
