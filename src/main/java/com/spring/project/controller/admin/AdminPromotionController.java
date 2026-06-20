package com.spring.project.controller.admin;

import com.spring.project.dto.PromotionRequest;
import com.spring.project.entity.Promotion;
import com.spring.project.service.PromotionService;
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

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/promotions")
public class AdminPromotionController {

    private final PromotionService promotionService;

    public AdminPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public String promotionList(@RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Promotion> promotionPage = promotionService.getPromotionList(status, pageable);
        model.addAttribute("promotionPage", promotionPage);
        model.addAttribute("status", status);
        return "admin/pages/promotionlist";
    }

    @GetMapping("/create")
    public String promotionCreate(Model model) {
        if (!model.containsAttribute("promotionRequest")) {
            model.addAttribute("promotionRequest", new PromotionRequest());
        }
        return "admin/pages/promotioncreate";
    }

    @PostMapping("/create")
    public String promotionCreatePost(@Valid @ModelAttribute("promotionRequest") PromotionRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/promotioncreate";
        }
        try {
            promotionService.createPromotion(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/promotioncreate";
        }
    }

    @GetMapping("/update")
    public String promotionUpdate(@RequestParam Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            if (!model.containsAttribute("promotionRequest")) {
                PromotionRequest req = new PromotionRequest();
                req.setCode(promotion.getCode());
                req.setName(promotion.getName());
                req.setDescription(promotion.getDescription());
                req.setDiscountType(promotion.getDiscountType());
                req.setDiscountValue(promotion.getDiscountValue());
                req.setMaxDiscountAmount(promotion.getMaxDiscountAmount());
                req.setMinBookingAmount(promotion.getMinBookingAmount());
                req.setStartDate(promotion.getStartDate());
                req.setEndDate(promotion.getEndDate());
                req.setUsageLimit(promotion.getUsageLimit());
                req.setStatus(promotion.getStatus());
                model.addAttribute("promotionRequest", req);
            }
            return "admin/pages/promotionupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
            return "redirect:/admin/promotions";
        }
    }

    @PostMapping("/update")
    public String promotionUpdatePost(@RequestParam Long id,
                                       @Valid @ModelAttribute("promotionRequest") PromotionRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("promotion", promotionService.getPromotionById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/promotionupdate";
        }
        try {
            promotionService.updatePromotion(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/promotions/update?id=" + id;
        }
    }

    @GetMapping("/delete")
    public String promotionDelete(@RequestParam Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            return "admin/pages/promotiondelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
            return "redirect:/admin/promotions";
        }
    }

    @PostMapping("/delete")
    public String promotionDeletePost(@RequestParam Long id,
                                       RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khuyến mãi thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khuyến mãi không tồn tại.");
        }
        return "redirect:/admin/promotions";
    }
}
