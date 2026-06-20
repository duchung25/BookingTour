package com.spring.project.controller.admin;

import com.spring.project.dto.RegisterRequest;
import com.spring.project.entity.User;
import com.spring.project.service.AuthService;
import com.spring.project.service.CustomerService;
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
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final CustomerService customerService;
    private final AuthService authService;

    public AdminCustomerController(CustomerService customerService, AuthService authService) {
        this.customerService = customerService;
        this.authService = authService;
    }

    @GetMapping
    public String customerList(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<User> customerPage = customerService.getCustomerList(keyword, pageable);
        model.addAttribute("customerPage", customerPage);
        model.addAttribute("keyword", keyword);
        return "admin/pages/customerlist";
    }

    @GetMapping("/status")
    public String customerStatus(@RequestParam Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            User customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            return "admin/pages/customerstatus";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @PostMapping("/status")
    public String customerToggle(@RequestParam Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            customerService.toggleCustomerStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng.");
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/create")
    public String customerCreate(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "admin/pages/customercreate";
    }

    @PostMapping("/create")
    public String customerCreatePost(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/customers/create";
        }

        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
            return "redirect:/admin/customers/create";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("registerRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/customers/create";
        }
    }
}
