package ca.mcgill.ecse321.gameorganizer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.gameorganizer.dto.response.PortfolioSummaryDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.PriceHistoryPointDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.ThemeBreakdownDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.SetPriceHistoryRepository;

@Service
public class CollectionAnalyticsService {

    private final CollectionItemRepository collectionItemRepository;
    private final SetPriceHistoryRepository setPriceHistoryRepository;
    private final LegoSetRepository legoSetRepository;

    @Autowired
    public CollectionAnalyticsService(
            CollectionItemRepository collectionItemRepository,
            SetPriceHistoryRepository setPriceHistoryRepository,
            LegoSetRepository legoSetRepository) {
        this.collectionItemRepository = collectionItemRepository;
        this.setPriceHistoryRepository = setPriceHistoryRepository;
        this.legoSetRepository = legoSetRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioSummaryDto getPortfolioSummary(String ownerEmail) {
        List<CollectionItem> items = collectionItemRepository.findByOwnerEmail(ownerEmail);

        int totalSets = items.size();
        int totalPieces = 0;
        BigDecimal totalCostCad = BigDecimal.ZERO;
        BigDecimal estimatedMarketValueCad = BigDecimal.ZERO;

        Map<String, ThemeAccumulator> themeMap = new LinkedHashMap<>();
        Map<ItemCondition, Integer> conditionBreakdown = new EnumMap<>(ItemCondition.class);

        for (CollectionItem item : items) {
            LegoSet set = item.getLegoSet();
            int pieces = set != null ? set.getPieceCount() : 0;
            String theme = (set != null && set.getTheme() != null) ? set.getTheme() : "Unknown";

            BigDecimal cost = resolveCost(item, set);
            BigDecimal marketValue = resolveMarketValue(item, set);

            totalPieces += pieces;
            totalCostCad = totalCostCad.add(cost);
            estimatedMarketValueCad = estimatedMarketValueCad.add(marketValue);

            ThemeAccumulator acc = themeMap.computeIfAbsent(theme, k -> new ThemeAccumulator(theme));
            acc.setCount++;
            acc.pieceCount += pieces;
            acc.totalValueCad = acc.totalValueCad.add(marketValue);

            ItemCondition condition = item.getCondition() != null ? item.getCondition() : ItemCondition.BUILT_DISPLAY;
            conditionBreakdown.merge(condition, 1, Integer::sum);
        }

        BigDecimal netGainCad = estimatedMarketValueCad.subtract(totalCostCad);
        double roiPercentage = 0.0;
        if (totalCostCad.compareTo(BigDecimal.ZERO) > 0) {
            roiPercentage = netGainCad
                    .divide(totalCostCad, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        List<ThemeBreakdownDto> themeBreakdown = new ArrayList<>();
        for (ThemeAccumulator acc : themeMap.values()) {
            themeBreakdown.add(new ThemeBreakdownDto(
                    acc.theme,
                    acc.setCount,
                    acc.pieceCount,
                    acc.totalValueCad.setScale(2, RoundingMode.HALF_UP)));
        }
        themeBreakdown.sort(Comparator.comparing(ThemeBreakdownDto::getTotalValueCad).reversed());

        return new PortfolioSummaryDto(
                totalSets,
                totalPieces,
                totalCostCad.setScale(2, RoundingMode.HALF_UP),
                estimatedMarketValueCad.setScale(2, RoundingMode.HALF_UP),
                netGainCad.setScale(2, RoundingMode.HALF_UP),
                roiPercentage,
                themeBreakdown,
                conditionBreakdown);
    }

    @Transactional(readOnly = true)
    public List<PriceHistoryPointDto> getSetPriceHistory(int legoSetId) {
        if (!legoSetRepository.existsById(legoSetId)) {
            throw new ResourceNotFoundException("LEGO set not found with id: " + legoSetId);
        }
        return setPriceHistoryRepository.findByLegoSetIdOrderByRecordedAtAsc(legoSetId).stream()
                .map(PriceHistoryPointDto::new)
                .collect(Collectors.toList());
    }

    private BigDecimal resolveCost(CollectionItem item, LegoSet set) {
        if (item.getPurchasePriceCad() != null) {
            return item.getPurchasePriceCad();
        }
        if (set != null && set.getRetailPriceCad() != null) {
            return set.getRetailPriceCad();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal resolveMarketValue(CollectionItem item, LegoSet set) {
        if (set != null && set.getEstimatedResaleValueCad() != null) {
            return set.getEstimatedResaleValueCad();
        }
        if (set != null && set.getRetailPriceCad() != null) {
            return set.getRetailPriceCad();
        }
        if (item.getPurchasePriceCad() != null) {
            return item.getPurchasePriceCad();
        }
        return BigDecimal.ZERO;
    }

    private static class ThemeAccumulator {
        final String theme;
        int setCount = 0;
        int pieceCount = 0;
        BigDecimal totalValueCad = BigDecimal.ZERO;

        ThemeAccumulator(String theme) {
            this.theme = theme;
        }
    }
}
