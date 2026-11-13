package ca.mcgill.ecse321.gameorganizer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubmissionDto {
    private int rating;
    private String comment;
    private int legoSetId;
    private String reviewerId;
}
