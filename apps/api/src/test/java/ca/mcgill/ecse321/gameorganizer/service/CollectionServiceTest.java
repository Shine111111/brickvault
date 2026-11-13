package ca.mcgill.ecse321.gameorganizer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.mcgill.ecse321.gameorganizer.dto.request.CollectionItemRequestDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.CollectionItemResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ForbiddenException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.AccountRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.services.CollectionService;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceTest {

    @Mock
    private CollectionItemRepository collectionItemRepository;

    @Mock
    private LegoSetRepository legoSetRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CollectionService collectionService;

    private Account owner;
    private LegoSet legoSet;
    private CollectionItem item;

    @BeforeEach
    public void setUp() {
        owner = new Account("Alice", "alice@brickvault.ca", "pw");
        owner.setId(10);

        legoSet = new LegoSet(
                "71799", "NINJAGO City Markets", "Ninjago", 6163, 2023,
                BigDecimal.valueOf(479.99), BigDecimal.valueOf(520.00),
                RetirementStatus.ACTIVE, null, "Modular set");
        legoSet.setId(1);

        item = new CollectionItem(
                legoSet, owner, ItemCondition.SEALED_NISB,
                BigDecimal.valueOf(450.00), LocalDate.of(2024, 5, 1),
                true, true, "Sealed");
        item.setId(100);
    }

    @Test
    public void testAddCollectionItemSuccess() {
        CollectionItemRequestDto dto = new CollectionItemRequestDto(
                1, ItemCondition.SEALED_NISB, BigDecimal.valueOf(450.00),
                LocalDate.of(2024, 5, 1), true, true, "Sealed");

        when(accountRepository.findByEmail("alice@brickvault.ca")).thenReturn(Optional.of(owner));
        when(legoSetRepository.findById(1)).thenReturn(Optional.of(legoSet));
        when(collectionItemRepository.save(any(CollectionItem.class))).thenReturn(item);

        CollectionItemResponseDto result = collectionService.addCollectionItem("alice@brickvault.ca", dto);

        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals(ItemCondition.SEALED_NISB, result.getCondition());
        assertEquals("71799", result.getLegoSet().getSetNumber());
    }

    @Test
    public void testAddCollectionItemSetNotFound() {
        CollectionItemRequestDto dto = new CollectionItemRequestDto();
        dto.setLegoSetId(999);

        when(accountRepository.findByEmail("alice@brickvault.ca")).thenReturn(Optional.of(owner));
        when(legoSetRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> collectionService.addCollectionItem("alice@brickvault.ca", dto));
    }

    @Test
    public void testUpdateCollectionItemForbiddenForNonOwner() {
        CollectionItemRequestDto dto = new CollectionItemRequestDto();
        dto.setCondition(ItemCondition.BUILT_DISPLAY);

        when(collectionItemRepository.findById(100)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> collectionService.updateCollectionItem(100, "other@brickvault.ca", dto));
    }

    @Test
    public void testDeleteCollectionItemSuccess() {
        when(collectionItemRepository.findById(100)).thenReturn(Optional.of(item));

        collectionService.deleteCollectionItem(100, "alice@brickvault.ca");

        verify(collectionItemRepository).delete(item);
    }

    @Test
    public void testGetItemsForTradeOrSale() {
        when(collectionItemRepository.findByForTradeOrSaleTrue()).thenReturn(List.of(item));

        List<CollectionItemResponseDto> items = collectionService.getItemsForTradeOrSale();

        assertEquals(1, items.size());
        assertEquals(100, items.get(0).getId());
    }
}
