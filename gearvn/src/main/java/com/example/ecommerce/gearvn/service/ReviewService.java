package com.example.ecommerce.gearvn.service;

import com.example.ecommerce.gearvn.model.Review;
import com.example.ecommerce.gearvn.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        Review existingReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new IllegalStateException("Review with ID " + review.getId() + " does not exist."));
        existingReview.setProduct(review.getProduct());
        existingReview.setUser(review.getUser());
        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        existingReview.setDate(review.getDate());
        return reviewRepository.save(existingReview);
    }

    public void deleteReviewById(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalStateException("Review with ID " + id + " does not exist.");
        }
        reviewRepository.deleteById(id);
    }
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
