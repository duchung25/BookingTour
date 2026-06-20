package com.spring.project.controller.admin;

import com.spring.project.dto.DestinationRequest;
import com.spring.project.entity.Destination;
import com.spring.project.service.DestinationService;
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
@RequestMapping("/admin/destinations")
public class AdminDestinationController {

    private final DestinationService destinationService;

    public AdminDestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    public String destinationList(@RequestParam(required = false) String status,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Destination> destinationPage = destinationService.getDestinationList(status, pageable);
        model.addAttribute("destinationPage", destinationPage);
        model.addAttribute("status", status);
        return "admin/pages/destinationlist";
    }

    @GetMapping("/create")
    public String destinationCreate(Model model) {
        if (!model.containsAttribute("destinationRequest")) {
            DestinationRequest req = new DestinationRequest();
            req.setCountry("Việt Nam");
            model.addAttribute("destinationRequest", req);
        }
        return "admin/pages/destinationcreate";
    }

    @PostMapping("/create")
    public String destinationCreatePost(@Valid @ModelAttribute("destinationRequest") DestinationRequest request,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/destinationcreate";
        }
        try {
            destinationService.createDestination(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo điểm đến thành công!");
            return "redirect:/admin/destinations";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/destinationcreate";
        }
    }

    @GetMapping("/update")
    public String destinationUpdate(@RequestParam Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Destination destination = destinationService.getDestinationById(id);
            model.addAttribute("destination", destination);
            if (!model.containsAttribute("destinationRequest")) {
                DestinationRequest req = new DestinationRequest();
                req.setName(destination.getName());
                req.setProvince(destination.getProvince());
                req.setCountry(destination.getCountry());
                req.setDescription(destination.getDescription());
                req.setImageUrl(destination.getImageUrl());
                req.setStatus(destination.getStatus());
                model.addAttribute("destinationRequest", req);
            }
            return "admin/pages/destinationupdate";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations";
        }
    }

    @PostMapping("/update")
    public String destinationUpdatePost(@RequestParam Long id,
                                         @Valid @ModelAttribute("destinationRequest") DestinationRequest request,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("destination", destinationService.getDestinationById(id));
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/destinationupdate";
        }
        try {
            destinationService.updateDestination(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật điểm đến thành công!");
            return "redirect:/admin/destinations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations/update?id=" + id;
        }
    }

    @GetMapping("/delete")
    public String destinationDelete(@RequestParam Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Destination destination = destinationService.getDestinationById(id);
            model.addAttribute("destination", destination);
            model.addAttribute("tourCount", destinationService.countTours(id));
            return "admin/pages/destinationdelete";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/destinations";
        }
    }

    @PostMapping("/delete")
    public String destinationDeletePost(@RequestParam Long id,
                                         RedirectAttributes redirectAttributes) {
        try {
            destinationService.deleteDestination(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý xóa điểm đến.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/destinations";
    }
}
