package com.alibou.book.Services;

import com.alibou.book.Entity.Product;
import com.alibou.book.Entity.Review;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.Repositories.ReviewRepository;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.security.UserDetailsServiceImpl;
import com.alibou.book.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserDetailsServiceImpl userDetailsServiceimpl;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

//    private final ProductRepository productRepository;
//    public Review createReview(Long productId, Review review) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
//        review.setProduct(product);
//        review.setCreatedAt(LocalDateTime.now());
//        return reviewRepository.save(review);
//    }

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }


    // DELETE A REVIEW IF YOU ARE AN ADMIN
    public void deleteReview(Long reviewId, Long userId) throws AccessDeniedException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
//         Check if the user is the owner of the review or has admin privileges
        if (!review.getUser().getId().equals(userId) && !userDetailsServiceimpl.isAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this review.");
        }
        reviewRepository.delete(review);
    }


    // UPDATE THE REVIEW
    public Review updateReview(Long reviewId, Long userId, int rating, String comment) throws AccessDeniedException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        // Check if the user is the owner of the review or has admin privileges
        if (!review.getUser().getId().equals(userId) && !userDetailsServiceimpl.isAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to update this review.");
        }
        // Update the review details
        review.setRating(rating);
        review.setComment(comment);
//        review.setUpdatedAt(LocalDateTime.now()); // Optional: Track update timestamp
        return reviewRepository.save(review);
    }

    //GET ALL REVIEWS BY A PRODUCT



    //GET REVIEWS BY USER
    public List<Review> getReviewsByUser(Long userId) {
        // Ensure the user exists before fetching reviews
        userRepository.findById(Math.toIntExact(userId)).orElseThrow(() ->
                new ResourceNotFoundException("User not found with ID: " + userId));
        return reviewRepository.findByUserId(userId);
    }
}
