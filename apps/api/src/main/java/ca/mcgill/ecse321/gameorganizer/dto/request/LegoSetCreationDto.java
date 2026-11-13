package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.math.BigDecimal;

import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegoSetCreationDto {

    @NotBlank(message = "Set number is required")
    private String setNumber;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Theme is required")
    private String theme;

    @Min(value = 1, message = "Piece count must be at least 1")
    private int pieceCount;

    @Min(value = 1949, message = "Release year must be 1949 or later")
    private int releaseYear;

    private BigDecimal retailPriceCad;

    private BigDecimal estimatedResaleValueCad;

    private RetirementStatus retirementStatus = RetirementStatus.ACTIVE;

    private String imageUrl;

    private String description;
}
