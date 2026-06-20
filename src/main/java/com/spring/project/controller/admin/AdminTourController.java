package com.spring.project.controller.admin;

import com.spring.project.dto.TourCreateRequest;
import com.spring.project.dto.TourDepartureRequest;
import com.spring.project.dto.TourUpdateRequest;
import com.spring.project.entity.Tour;
import com.spring.project.entity.TourDeparture;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.service.TourDepartureService;
import com.spring.project.service.TourService;
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
import java.util.List;

@Controller
@RequestMapping("/admin/tours")
public class AdminTourController {

    private final TourService tourService;
    private final TourDepartureService tourDepartureService;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;

    public AdminTourController(TourService tourService, TourDepartureService tourDepartureService,
                               TourCategoryRepository tourCategoryRepository, DestinationRepository destinationRepository) {
        this.tourService = tourService;
        this.tourDepartureService = tourDepartureService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
    }

    @GetMapping
    public String tourList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Tour> tourPage = tourService.getTourList(keyword, status, pageable);
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "admin/pages/tourlist";
    }

    @GetMapping("/create")
    public String tourCreate(Model model) {
        if (!model.containsAttribute("tourCreateRequest")) {
            model.addAttribute("tourCreateRequest", new TourCreateRequest());
        }
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "admin/pages/tourcreate";
    }

    @PostMapping("/create")
    public String tourCreatePost(@Valid @ModelAttribute("tourCreateRequest") TourCreateRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/tourcreate";
        }
        try {
            tourService.createTour(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tour thành công!");
            return "redirect:/admin/tours";
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/pages/tourcreate";
        }
    }

    @GetMapping("/update")
    public String tourUpdate(@RequestParam Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(id);
            model.addAttribute("tour", tour);
            if (!model.containsAttribute("tourUpdateRequest")) {
                TourUpdateRequest req = new TourUpdateRequest();
                req.setCategoryId(tour.getCategory().getId());
                req.setDestinationId(tour.getDestination().getId());
                req.setName(tour.getName());
                req.setSlug(tour.getSlug());
                req.setDepartureLocation(tour.getDepartureLocation());
                req.setDurationDays(tour.getDurationDays());
                req.setDurationNights(tour.getDurationNights());
                req.setTransport(tour.getTransport());
                req.setHotelStandard(tour.getHotelStandard());
                req.setDescription(tour.getDescription());
                req.setPolicy(tour.getPolicy());
                req.setIncludedServices(tour.getIncludedServices());
                req.setExcludedServices(tour.getExcludedServices());
                req.setNotes(tour.getNotes());
                req.setStatus(tour.getStatus());
                model.addAttribute("tourUpdateRequest", req);
            }
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            return "admin/pages/tourupdate";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/update")
    public String tourUpdatePost(@RequestParam Long id,
                                 @Valid @ModelAttribute("tourUpdateRequest") TourUpdateRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tour", tourService.getTourById(id));
            model.addAttribute("categories", tourCategoryRepository.findAll());
            model.addAttribute("destinations", destinationRepository.findAll());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "admin/pages/tourupdate";
        }
        try {
            tourService.updateTour(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tour thành công!");
            return "redirect:/admin/tours";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/tours/update?id=" + id;
        }
    }

    @GetMapping("/delete")
    public String tourDelete(@RequestParam Long id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(id);
            model.addAttribute("tour", tour);
            return "admin/pages/tourdelete";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/delete")
    public String tourDeletePost(@RequestParam Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            tourService.deleteTour(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tour đã được xóa!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
        }
        return "redirect:/admin/tours";
    }

    // ==================== DEPARTURE MANAGEMENT ====================

    @GetMapping("/departures")
    public String tourDepartures(@RequestParam Long tourId, Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Tour tour = tourService.getTourById(tourId);
            List<TourDeparture> departures = tourDepartureService.getDeparturesByTourId(tourId);
            model.addAttribute("tour", tour);
            model.addAttribute("departures", departures);
            if (!model.containsAttribute("departureRequest")) {
                TourDepartureRequest req = new TourDepartureRequest();
                req.setTourId(tourId);
                model.addAttribute("departureRequest", req);
            }
            return "admin/pages/tourdepartures";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại.");
            return "redirect:/admin/tours";
        }
    }

    @PostMapping("/departures/add")
    public String departureAdd(@Valid @ModelAttribute("departureRequest") TourDepartureRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/tours/departures?tourId=" + request.getTourId();
        }
        try {
            tourDepartureService.addDeparture(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyến thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tours/departures?tourId=" + request.getTourId();
    }

    @PostMapping("/departures/delete")
    public String departureDelete(@RequestParam Long id, @RequestParam Long tourId,
                                  RedirectAttributes redirectAttributes) {
        try {
            tourDepartureService.deleteDeparture(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyến thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tours/departures?tourId=" + tourId;
    }
}
