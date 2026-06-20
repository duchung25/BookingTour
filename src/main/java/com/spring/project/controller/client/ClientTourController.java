package com.spring.project.controller.client;

import com.spring.project.entity.Tour;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.ReviewRepository;
import com.spring.project.repository.TourCategoryRepository;
import com.spring.project.repository.TourDepartureRepository;
import com.spring.project.service.TourService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tours")
public class ClientTourController {

    private final TourService tourService;
    private final TourCategoryRepository tourCategoryRepository;
    private final DestinationRepository destinationRepository;
    private final TourDepartureRepository tourDepartureRepository;
    private final ReviewRepository reviewRepository;

    public ClientTourController(TourService tourService,
                                TourCategoryRepository tourCategoryRepository,
                                DestinationRepository destinationRepository,
                                TourDepartureRepository tourDepartureRepository,
                                ReviewRepository reviewRepository) {
        this.tourService = tourService;
        this.tourCategoryRepository = tourCategoryRepository;
        this.destinationRepository = destinationRepository;
        this.tourDepartureRepository = tourDepartureRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public String tourList(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, 9, Sort.by("createdAt").descending());
        Page<Tour> tourPage = tourService.searchToursForClient(
                keyword, destinationId, categoryId, minDuration, maxDuration, minPrice, maxPrice, pageable);
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("destinationId", destinationId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minDuration", minDuration);
        model.addAttribute("maxDuration", maxDuration);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categories", tourCategoryRepository.findAll());
        model.addAttribute("destinations", destinationRepository.findAll());
        return "client/pages/tourlist";
    }

    @GetMapping("/detail")
    public String tourDetail(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        Tour tour = tourService.getTourDetailForClient(id);
        if (tour == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tour không tồn tại hoặc đã ngừng hoạt động");
            return "redirect:/tours";
        }

        // Load departures còn slot (riêng để tránh MultipleBagFetchException)
        var departures = tourDepartureRepository.findAvailableDeparturesByTour(id, java.time.LocalDate.now());

        // Load reviews (VISIBLE only)
        var reviews = reviewRepository.findByTourIdAndStatusOrderByCreatedAtDesc(id, "VISIBLE");
        Double avgRating = reviewRepository.calculateAverageRatingByTourId(id);
        Long reviewCount = reviewRepository.countVisibleReviewsByTourId(id);

        model.addAttribute("tour", tour);
        model.addAttribute("departures", departures);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating != null ? String.format("%.1f", avgRating) : "0");
        model.addAttribute("reviewCount", reviewCount != null ? reviewCount : 0);

        return "client/pages/tourdetail";
    }
}
