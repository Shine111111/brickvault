package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItemRequestDto {

    @NotNull(message = "LEGO set ID is required")
    private Integer legoSetId;

    private ItemCondition condition = ItemCondition.BUILT_DISPLAY;

    private BigDecimal purchasePriceCad;

    private LocalDate purchaseDate;

    private boolean forTradeOrSale = false;

    private boolean available = true;

    private String notes;
}
