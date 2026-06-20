package com.spring.project.controller.admin;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.StaffService;
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
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private final StaffService staffService;

    public AdminStaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String staffList(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<User> staffPage = staffService.getStaffList(keyword, status, pageable);
        model.addAttribute("staffPage", staffPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentUserId", SecurityUtils.getCurrentUserId());
        return "admin/pages/stafflist";
    }

    @GetMapping("/create")
    public String staffCreate(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "admin/pages/staffcreate";
    }

    @PostMapping("/create")
    public String staffCreatePost(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/staff/create";
        }
        try {
            staffService.createStaff(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công!");
            return "redirect:/admin/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/staff/create";
        }
    }

    @GetMapping("/update")
    public String staffUpdate(@RequestParam Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            User staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            if (!model.containsAttribute("updateProfileRequest")) {
                UpdateProfileRequest profileReq = new UpdateProfileRequest();
                profileReq.setFullName(staff.getFullName());
                profileReq.setPhone(staff.getPhone());
                profileReq.setGender(staff.getGender());
                profileReq.setDateOfBirth(staff.getDateOfBirth() != null ? staff.getDateOfBirth().toString() : "");
                profileReq.setAddress(staff.getAddress());
                model.addAttribute("updateProfileRequest", profileReq);
            }
            return "admin/pages/staffupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
            return "redirect:/admin/staff";
        }
    }

    @PostMapping("/update")
    public String staffUpdatePost(@RequestParam Long id,
                                  @Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/staff/update?id=" + id;
        }
        try {
            staffService.updateStaff(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công!");
            return "redirect:/admin/staff";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/staff/update?id=" + id;
        }
    }

    @GetMapping("/delete")
    public String staffDelete(@RequestParam Long id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            User staff = staffService.getStaffById(id);
            model.addAttribute("staff", staff);
            model.addAttribute("currentUserId", SecurityUtils.getCurrentUserId());
            return "admin/pages/staffdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
            return "redirect:/admin/staff";
        }
    }

    @PostMapping("/delete")
    public String staffDeletePost(@RequestParam Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã chuyển trạng thái nghỉ việc!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nhân viên không tồn tại.");
        }
        return "redirect:/admin/staff";
    }
}
