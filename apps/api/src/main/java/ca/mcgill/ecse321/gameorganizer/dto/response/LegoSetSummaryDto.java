package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.math.BigDecimal;

import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegoSetSummaryDto {
    private int id;
    private String setNumber;
    private String name;
    private String theme;
    private int pieceCount;
    private int releaseYear;
    private BigDecimal retailPriceCad;
    private BigDecimal estimatedResaleValueCad;
    private RetirementStatus retirementStatus;
    private String imageUrl;

    public LegoSetSummaryDto(LegoSet set) {
        this.id = set.getId();
        this.setNumber = set.getSetNumber();
        this.name = set.getName();
        this.theme = set.getTheme();
        this.pieceCount = set.getPieceCount();
        this.releaseYear = set.getReleaseYear();
        this.retailPriceCad = set.getRetailPriceCad();
        this.estimatedResaleValueCad = set.getEstimatedResaleValueCad();
        this.retirementStatus = set.getRetirementStatus();
        this.imageUrl = set.getImageUrl();
    }
}
