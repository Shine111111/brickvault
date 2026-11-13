package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.util.Date;

import ca.mcgill.ecse321.gameorganizer.models.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewResponseDto {
    private int id;
    private int rating;
    private String comment;
    private Date dateSubmitted;
    private Integer legoSetId;
    private String setNumber;
    private String setName;
    private AccountDto reviewer;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.dateSubmitted = review.getDateSubmitted();

        if (review.getLegoSet() != null) {
            this.legoSetId = review.getLegoSet().getId();
            this.setNumber = review.getLegoSet().getSetNumber();
            this.setName = review.getLegoSet().getName();
        }

        if (review.getReviewer() != null) {
            this.reviewer = new AccountDto(review.getReviewer());
        }
    }
}
