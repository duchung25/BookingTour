package com.spring.project.controller.admin;

import com.spring.project.dto.TourCategoryRequest;
import com.spring.project.entity.TourCategory;
import com.spring.project.service.TourCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final TourCategoryService tourCategoryService;

    public AdminCategoryController(TourCategoryService tourCategoryService) {
        this.tourCategoryService = tourCategoryService;
    }

    @GetMapping
    public String categoryList(@RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<TourCategory> categoryPage = tourCategoryService.getCategoryList(status, pageable);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("status", status);
        return "admin/pages/categorylist";
    }

    @GetMapping("/create")
    public String categoryCreate(Model model) {
        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest", new TourCategoryRequest());
        }
        return "admin/pages/categorycreate";
    }

    @PostMapping("/create")
    public String categoryCreatePost(@Valid @ModelAttribute("categoryRequest") TourCategoryRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/categorycreate";
        }
        try {
            tourCategoryService.createCategory(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/categorycreate";
        }
    }

    @GetMapping("/update")
    public String categoryUpdate(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            TourCategory category = tourCategoryService.getCategoryById(id);
            model.addAttribute("category", category);
            if (!model.containsAttribute("categoryRequest")) {
                TourCategoryRequest req = new TourCategoryRequest();
                req.setName(category.getName());
                req.setDescription(category.getDescription());
                req.setStatus(category.getStatus());
                model.addAttribute("categoryRequest", req);
            }
            return "admin/pages/categoryupdate";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/update")
    public String categoryUpdatePost(@RequestParam Long id,
                                      @Valid @ModelAttribute("categoryRequest") TourCategoryRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", tourCategoryService.getCategoryById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/categoryupdate";
        }
        try {
            tourCategoryService.updateCategory(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories/update?id=" + id;
        }
    }

    @GetMapping("/delete")
    public String categoryDelete(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            TourCategory category = tourCategoryService.getCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("tourCount", tourCategoryService.countTours(id));
            return "admin/pages/categorydelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/delete")
    public String categoryDeletePost(@RequestParam Long id,
                                      RedirectAttributes redirectAttributes) {
        try {
            tourCategoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý xóa danh mục.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
