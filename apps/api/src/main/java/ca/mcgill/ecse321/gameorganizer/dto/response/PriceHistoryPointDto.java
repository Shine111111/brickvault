package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import ca.mcgill.ecse321.gameorganizer.models.SetPriceHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryPointDto {
    private int id;
    private int legoSetId;
    private LocalDate recordedAt;
    private BigDecimal retailPriceCad;
    private BigDecimal estimatedMarketValueCad;

    public PriceHistoryPointDto(SetPriceHistory history) {
        this.id = history.getId();
        this.legoSetId = history.getLegoSet().getId();
        this.recordedAt = history.getRecordedAt();
        this.retailPriceCad = history.getRetailPriceCad();
        this.estimatedMarketValueCad = history.getEstimatedMarketValueCad();
    }
}
