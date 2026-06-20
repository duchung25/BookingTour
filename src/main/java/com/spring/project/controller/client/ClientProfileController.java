package com.spring.project.controller.client;

import com.spring.project.dto.ChangePasswordRequest;
import com.spring.project.dto.UpdateProfileRequest;
import com.spring.project.entity.User;
import com.spring.project.security.SecurityUtils;
import com.spring.project.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ClientProfileController {

    private final UserService userService;

    public ClientProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(@RequestParam(required = false) String success,
            @RequestParam(required = false) String passwordChanged,
            @RequestParam(required = false) String forceChange,
            HttpSession session,
            Model model) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);
        boolean hasLocalProvider = userService.hasLocalProvider(userId);

        model.addAttribute("user", user);
        model.addAttribute("hasLocalProvider", hasLocalProvider);

        // Kiểm tra cờ session: vừa reset mật khẩu → ép đổi mật khẩu mới
        String justReset = (String) session.getAttribute("justResetPasswordEmail");
        if (justReset != null && justReset.equalsIgnoreCase(user.getEmail())) {
            model.addAttribute("forceChange", true);
        }

        // Thêm DTO rỗng nếu chưa có trong model (lần đầu vào trang)
        if (!model.containsAttribute("updateProfileRequest")) {
            UpdateProfileRequest profileReq = new UpdateProfileRequest();
            profileReq.setFullName(user.getFullName());
            profileReq.setPhone(user.getPhone());
            profileReq.setGender(user.getGender());
            profileReq.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "");
            profileReq.setAddress(user.getAddress());
            model.addAttribute("updateProfileRequest", profileReq);
        }
        if (!model.containsAttribute("changePasswordRequest")) {
            model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        }

        // Thông báo thành công
        if (success != null)
            model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
        if (passwordChanged != null)
            model.addAttribute("successMessage", "Đổi mật khẩu thành công!");

        return "client/pages/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            User updatedUser = userService.updateProfile(userId, request);

            // Đồng bộ lại principal trong SecurityContextHolder
            SecurityUtils.refreshAuthentication(updatedUser);

            return "redirect:/profile?success=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra cờ session: vừa reset mật khẩu → bypass kiểm tra mật khẩu cũ
        String justReset = (String) session.getAttribute("justResetPasswordEmail");
        Long userId = SecurityUtils.getCurrentUserId();
        User currentUser = userService.getUserById(userId);

        if (justReset != null && justReset.equalsIgnoreCase(currentUser.getEmail())) {
            // Chỉ validate mật khẩu mới
            if (bindingResult.hasFieldErrors("newPassword") || bindingResult.hasFieldErrors("confirmNewPassword")) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest",
                        bindingResult);
                redirectAttributes.addFlashAttribute("changePasswordRequest", request);
                redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
                return "redirect:/profile?forceChange=true";
            }
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu xác nhận không khớp");
                redirectAttributes.addFlashAttribute("changePasswordRequest", request);
                return "redirect:/profile?forceChange=true";
            }

            // Cập nhật trực tiếp không cần kiểm tra mật khẩu cũ
            userService.resetPassword(currentUser.getEmail(), request.getNewPassword());
            session.removeAttribute("justResetPasswordEmail");
            return "redirect:/profile?passwordChanged=true";
        }

        // Luồng bình thường: đổi mật khẩu có kiểm tra mật khẩu cũ
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
            userService.changePassword(userId, request);
            return "redirect:/profile?passwordChanged=true";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
            return "redirect:/profile";
        }
    }
}
