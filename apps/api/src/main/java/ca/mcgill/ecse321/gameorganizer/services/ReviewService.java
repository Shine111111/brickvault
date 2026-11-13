package ca.mcgill.ecse321.gameorganizer.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.gameorganizer.dto.request.ReviewSubmissionDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.ReviewResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ForbiddenException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.Review;
import ca.mcgill.ecse321.gameorganizer.repositories.AccountRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LegoSetRepository legoSetRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public ReviewService(
            ReviewRepository reviewRepository,
            LegoSetRepository legoSetRepository,
            AccountRepository accountRepository) {
        this.reviewRepository = reviewRepository;
        this.legoSetRepository = legoSetRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByLegoSetId(int legoSetId) {
        return reviewRepository.findByLegoSetId(legoSetId).stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByReviewerEmail(String email) {
        return reviewRepository.findReviewsByReviewerEmail(email).stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(int id) {
        Review review = reviewRepository.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return new ReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto submitReview(String reviewerEmail, ReviewSubmissionDto dto) {
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Account reviewer = accountRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer account not found: " + reviewerEmail));
        LegoSet legoSet = legoSetRepository.findById(dto.getLegoSetId())
                .orElseThrow(() -> new ResourceNotFoundException("LEGO set not found with id: " + dto.getLegoSetId()));

        Review review = new Review(dto.getRating(), dto.getComment(), new Date());
        review.setLegoSet(legoSet);
        review.setReviewer(reviewer);
        return new ReviewResponseDto(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDto updateReview(int id, String requesterEmail, ReviewSubmissionDto dto) {
        Review review = reviewRepository.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        if (review.getReviewer() == null || !review.getReviewer().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ForbiddenException("You can only update your own reviews");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return new ReviewResponseDto(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(int id, String requesterEmail) {
        Review review = reviewRepository.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        if (review.getReviewer() == null || !review.getReviewer().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }
}
