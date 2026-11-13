package ca.mcgill.ecse321.gameorganizer.dto.request;

import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegoSetSearchCriteria {
    private String theme;
    private RetirementStatus retirementStatus;
    private String setNumber;
    private String name;
    private Integer minPieces;
    private Integer maxPieces;
}
