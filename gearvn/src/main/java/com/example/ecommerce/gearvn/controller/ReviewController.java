package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.Review;
import com.example.ecommerce.gearvn.service.ProductService;
import com.example.ecommerce.gearvn.service.ReviewService;
import com.example.ecommerce.gearvn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, ProductService productService, UserService userService) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String showReviewList(Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "reviews/review-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("review", new Review());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("users", userService.getAllUsers());
        return "reviews/add-review";
    }

    @PostMapping("/add")
    public String addReview(@Valid @ModelAttribute Review review, BindingResult result) {
        if (result.hasErrors()) {
            return "reviews/add-review";
        }
        review.setDate(LocalDate.now());
        reviewService.addReview(review);
        return "redirect:/reviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + id));
        model.addAttribute("review", review);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("users", userService.getAllUsers());
        return "reviews/update-review";
    }

    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Long id, @Valid @ModelAttribute Review review, BindingResult result) {
        if (result.hasErrors()) {
            review.setId(id); // giữ id trong form
            return "reviews/update-review";
        }
        reviewService.updateReview(review);
        return "redirect:/reviews";
    }

    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return "redirect:/reviews";
    }
    @GetMapping("/product/{productId}")
    public String getReviewsByProductId(@PathVariable Long productId, Model model) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        model.addAttribute("reviews", reviews);
        return "product-reviews";
    }

}
