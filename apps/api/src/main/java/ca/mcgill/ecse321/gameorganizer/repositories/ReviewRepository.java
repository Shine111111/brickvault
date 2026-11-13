package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Review> findReviewById(int id);

    List<Review> findByLegoSet(LegoSet legoSet);

    List<Review> findByLegoSetId(int legoSetId);

    @Query("SELECT r FROM Review r WHERE LOWER(r.legoSet.name) LIKE LOWER(CONCAT('%', :setName, '%'))")
    List<Review> findBySetName(@Param("setName") String setName);

    @Query("SELECT r FROM Review r WHERE LOWER(r.legoSet.theme) = LOWER(:theme)")
    List<Review> findBySetTheme(@Param("theme") String theme);

    List<Review> findByReviewer(Account reviewer);

    List<Review> findByRating(int rating);

    List<Review> findByRatingGreaterThanEqual(int minRating);

    List<Review> findReviewsByReviewerName(String username);

    List<Review> findReviewsByReviewerEmail(String email);
}
