package ca.mcgill.ecse321.gameorganizer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.mcgill.ecse321.gameorganizer.dto.response.PortfolioSummaryDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.PriceHistoryPointDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.models.SetPriceHistory;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.SetPriceHistoryRepository;
import ca.mcgill.ecse321.gameorganizer.services.CollectionAnalyticsService;

@ExtendWith(MockitoExtension.class)
public class CollectionAnalyticsServiceTest {

    @Mock
    private CollectionItemRepository collectionItemRepository;

    @Mock
    private SetPriceHistoryRepository setPriceHistoryRepository;

    @Mock
    private LegoSetRepository legoSetRepository;

    @InjectMocks
    private CollectionAnalyticsService analyticsService;

    private Account owner;
    private LegoSet ninjagoSet;
    private LegoSet starWarsSet;

    @BeforeEach
    public void setUp() {
        owner = new Account("Alice", "alice@brickvault.ca", "pw");
        owner.setId(1);

        ninjagoSet = new LegoSet(
                "71799", "NINJAGO City Markets", "Ninjago", 6163, 2023,
                BigDecimal.valueOf(479.99), BigDecimal.valueOf(520.00),
                RetirementStatus.ACTIVE, null, "Modular");
        ninjagoSet.setId(10);

        starWarsSet = new LegoSet(
                "75192", "Millennium Falcon", "Star Wars", 7541, 2017,
                BigDecimal.valueOf(1049.99), BigDecimal.valueOf(1120.00),
                RetirementStatus.ACTIVE, null, "UCS");
        starWarsSet.setId(20);
    }

    @Test
    public void testGetPortfolioSummaryCalculatesTotalsRoiAndBreakdowns() {
        CollectionItem item1 = new CollectionItem(
                ninjagoSet, owner, ItemCondition.SEALED_NISB,
                BigDecimal.valueOf(400.00), LocalDate.of(2024, 1, 10),
                true, true, "Sealed");
        CollectionItem item2 = new CollectionItem(
                starWarsSet, owner, ItemCondition.BUILT_DISPLAY,
                BigDecimal.valueOf(1000.00), LocalDate.of(2024, 2, 10),
                false, true, "Displayed");

        when(collectionItemRepository.findByOwnerEmail("alice@brickvault.ca"))
                .thenReturn(List.of(item1, item2));

        PortfolioSummaryDto summary = analyticsService.getPortfolioSummary("alice@brickvault.ca");

        assertEquals(2, summary.getTotalSets());
        assertEquals(13704, summary.getTotalPieces());
        assertEquals(new BigDecimal("1400.00"), summary.getTotalCostCad());
        assertEquals(new BigDecimal("1640.00"), summary.getEstimatedMarketValueCad());
        assertEquals(new BigDecimal("240.00"), summary.getNetGainCad());
        assertEquals(17.14, summary.getRoiPercentage());

        assertEquals(2, summary.getThemeBreakdown().size());
        assertEquals("Star Wars", summary.getThemeBreakdown().get(0).getTheme());
        assertEquals(new BigDecimal("1120.00"), summary.getThemeBreakdown().get(0).getTotalValueCad());
        assertEquals("Ninjago", summary.getThemeBreakdown().get(1).getTheme());

        assertEquals(1, summary.getConditionBreakdown().get(ItemCondition.SEALED_NISB));
        assertEquals(1, summary.getConditionBreakdown().get(ItemCondition.BUILT_DISPLAY));
    }

    @Test
    public void testGetPortfolioSummaryEmptyCollection() {
        when(collectionItemRepository.findByOwnerEmail("empty@brickvault.ca")).thenReturn(List.of());

        PortfolioSummaryDto summary = analyticsService.getPortfolioSummary("empty@brickvault.ca");

        assertEquals(0, summary.getTotalSets());
        assertEquals(0, summary.getTotalPieces());
        assertEquals(new BigDecimal("0.00"), summary.getTotalCostCad());
        assertEquals(new BigDecimal("0.00"), summary.getEstimatedMarketValueCad());
        assertEquals(new BigDecimal("0.00"), summary.getNetGainCad());
        assertEquals(0.0, summary.getRoiPercentage());
    }

    @Test
    public void testGetSetPriceHistorySuccessAndNotFound() {
        when(legoSetRepository.existsById(10)).thenReturn(true);
        when(setPriceHistoryRepository.findByLegoSetIdOrderByRecordedAtAsc(10))
                .thenReturn(List.of(new SetPriceHistory(
                        ninjagoSet, LocalDate.of(2025, 1, 1),
                        BigDecimal.valueOf(479.99), BigDecimal.valueOf(520.00))));

        List<PriceHistoryPointDto> history = analyticsService.getSetPriceHistory(10);
        assertEquals(1, history.size());
        assertEquals(BigDecimal.valueOf(520.00), history.get(0).getEstimatedMarketValueCad());

        when(legoSetRepository.existsById(999)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> analyticsService.getSetPriceHistory(999));
    }
}
