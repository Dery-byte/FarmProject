package com.alibou.book.Controllers;

import com.alibou.book.Entity.Product;
import com.alibou.book.Entity.Review;
import com.alibou.book.Repositories.ProductRepository;
import com.alibou.book.Repositories.ReviewRepository;
import com.alibou.book.Services.ReviewService;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserDetailsService userDetailsService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;


    //To give Review the id of the product should be passed as a path variable in the URL
    @PostMapping("/add/{productId}")
    public Review createReview(@PathVariable Long productId, @RequestBody Review review, Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.badRequest().body("Principal is null");
//        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        User user= (User) this.userDetailsService.loadUserByUsername(principal.getName());
        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());
       return reviewRepository.save(review);
//        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }


    //Get review by Product ID
    @GetMapping("/reviewByProduct/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

//    Delete Review by ID
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
           Principal principal
    ) throws AccessDeniedException {
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName());
        reviewService.deleteReview(reviewId, Long.valueOf(user.getId()));
        return ResponseEntity.noContent().build();
    }

    //UPDATE REVIEW
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> payload, // Using JSON request body
            Principal principal
    ) throws AccessDeniedException {
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName()); // Get logged-in user
        int rating = (int) payload.get("rating");
        String comment = (String) payload.get("comment");
        Review updatedReview = reviewService.updateReview(reviewId, Long.valueOf(user.getId()), rating, comment);
        return ResponseEntity.ok(updatedReview);
    }

    // GET REVIIEW BY A USER
     @GetMapping("/user/{userId}")
     public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
         List<Review> reviews = reviewService.getReviewsByUser(userId);
         return ResponseEntity.ok(reviews);
     }


}
