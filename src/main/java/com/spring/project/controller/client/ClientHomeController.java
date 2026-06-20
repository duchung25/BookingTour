package com.spring.project.controller.client;

import com.spring.project.entity.Tour;
import com.spring.project.repository.DestinationRepository;
import com.spring.project.repository.ReviewRepository;
import com.spring.project.service.TourService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class ClientHomeController {

    private final TourService tourService;
    private final DestinationRepository destinationRepository;
    private final ReviewRepository reviewRepository;

    public ClientHomeController(TourService tourService,
                                DestinationRepository destinationRepository,
                                ReviewRepository reviewRepository) {
        this.tourService = tourService;
        this.destinationRepository = destinationRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        // Tour nổi bật (4 tour mới nhất)
        Pageable hotPageable = PageRequest.of(0, 4, Sort.by("id").descending());
        Page<Tour> hotPage = tourService.searchToursForClient(null, null, null, null, null, null, null, hotPageable);
        model.addAttribute("hotTours", hotPage.getContent());

        // Tour khuyến mãi (3 tour cũ nhất)
        Pageable promoPageable = PageRequest.of(0, 3, Sort.by("id").ascending());
        Page<Tour> promoPage = tourService.searchToursForClient(null, null, null, null, null, null, null, promoPageable);
        model.addAttribute("promoTours", promoPage.getContent());

        // Điểm đến phổ biến
        model.addAttribute("destinations", destinationRepository.findByStatus("ACTIVE"));

        // Đánh giá gần nhất
        Pageable reviewPageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        model.addAttribute("reviews", reviewRepository.findRecentVisibleReviews(reviewPageable).getContent());

        return "client/pages/home";
    }

    @GetMapping("/about")
    public String about() {
        return "client/pages/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "client/pages/contact";
    }
}
