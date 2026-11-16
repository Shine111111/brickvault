package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDto {
    private int totalSets;
    private int totalPieces;
    private BigDecimal totalCostCad;
    private BigDecimal estimatedMarketValueCad;
    private BigDecimal netGainCad;
    private double roiPercentage;
    private List<ThemeBreakdownDto> themeBreakdown;
    private Map<ItemCondition, Integer> conditionBreakdown;
}
