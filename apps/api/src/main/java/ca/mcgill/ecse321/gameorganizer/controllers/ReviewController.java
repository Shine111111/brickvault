package ca.mcgill.ecse321.gameorganizer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.gameorganizer.dto.request.ReviewSubmissionDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.ReviewResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.UnauthedException;
import ca.mcgill.ecse321.gameorganizer.services.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(
            @RequestParam(required = false) Integer legoSetId,
            @RequestParam(required = false) String reviewerEmail) {
        if (legoSetId != null) {
            return ResponseEntity.ok(reviewService.getReviewsByLegoSetId(legoSetId));
        }
        if (reviewerEmail != null && !reviewerEmail.isBlank()) {
            return ResponseEntity.ok(reviewService.getReviewsByReviewerEmail(reviewerEmail));
        }
        String email = requireAuthenticatedEmail();
        return ResponseEntity.ok(reviewService.getReviewsByReviewerEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> submitReview(@RequestBody ReviewSubmissionDto dto) {
        String email = requireAuthenticatedEmail();
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.submitReview(email, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable int id,
            @RequestBody ReviewSubmissionDto dto) {
        String email = requireAuthenticatedEmail();
        return ResponseEntity.ok(reviewService.updateReview(id, email, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        String email = requireAuthenticatedEmail();
        reviewService.deleteReview(id, email);
        return ResponseEntity.noContent().build();
    }

    private String requireAuthenticatedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthedException("Authentication required");
        }
        return auth.getName();
    }
}
