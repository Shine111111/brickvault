package ca.mcgill.ecse321.gameorganizer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.AccountRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@DataJpaTest
@ActiveProfiles("test")
public class CollectionItemRepositoryTests {

    @Autowired
    private CollectionItemRepository collectionItemRepository;

    @Autowired
    private LegoSetRepository legoSetRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account owner;
    private Account otherOwner;
    private LegoSet ninjagoSet;
    private LegoSet architectureSet;

    @BeforeEach
    public void setUp() {
        owner = accountRepository.save(new Account("Collector One", "collector1@brickvault.ca", "pw"));
        otherOwner = accountRepository.save(new Account("Collector Two", "collector2@brickvault.ca", "pw"));

        ninjagoSet = legoSetRepository.save(new LegoSet(
                "71799",
                "NINJAGO City Markets",
                "Ninjago",
                6163,
                2023,
                BigDecimal.valueOf(479.99),
                BigDecimal.valueOf(520.00),
                RetirementStatus.ACTIVE,
                "https://example.com/71799.png",
                "Modular NINJAGO City market"));

        architectureSet = legoSetRepository.save(new LegoSet(
                "21057",
                "Singapore",
                "Architecture",
                827,
                2022,
                BigDecimal.valueOf(79.99),
                BigDecimal.valueOf(85.00),
                RetirementStatus.ACTIVE,
                "https://example.com/21057.png",
                "Singapore skyline"));

        collectionItemRepository.save(new CollectionItem(
                ninjagoSet, owner, ItemCondition.SEALED_NISB,
                BigDecimal.valueOf(450.00), LocalDate.of(2024, 1, 10),
                true, true, "Mint sealed box"));

        collectionItemRepository.save(new CollectionItem(
                architectureSet, owner, ItemCondition.BUILT_DISPLAY,
                BigDecimal.valueOf(79.99), LocalDate.of(2024, 2, 15),
                false, true, "On shelf"));

        collectionItemRepository.save(new CollectionItem(
                ninjagoSet, otherOwner, ItemCondition.LOOSE_COMPLETE,
                BigDecimal.valueOf(380.00), LocalDate.of(2024, 3, 5),
                true, false, "Checked out"));
    }

    @AfterEach
    public void tearDown() {
        collectionItemRepository.deleteAll();
        legoSetRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void testFindByOwnerEmailAndOwnerId() {
        List<CollectionItem> byEmail = collectionItemRepository.findByOwnerEmail("collector1@brickvault.ca");
        assertEquals(2, byEmail.size());

        List<CollectionItem> byId = collectionItemRepository.findByOwnerId(otherOwner.getId());
        assertEquals(1, byId.size());
        assertEquals(ItemCondition.LOOSE_COMPLETE, byId.get(0).getCondition());
    }

    @Test
    public void testFindByForTradeOrSaleTrue() {
        List<CollectionItem> tradeItems = collectionItemRepository.findByForTradeOrSaleTrue();
        assertEquals(2, tradeItems.size());
        assertTrue(tradeItems.stream().allMatch(CollectionItem::isForTradeOrSale));
    }

    @Test
    public void testFindByLegoSetIdAndCondition() {
        List<CollectionItem> bySet = collectionItemRepository.findByLegoSetId(ninjagoSet.getId());
        assertEquals(2, bySet.size());

        List<CollectionItem> byCondition = collectionItemRepository.findByCondition(ItemCondition.SEALED_NISB);
        assertEquals(1, byCondition.size());
        assertEquals("Mint sealed box", byCondition.get(0).getNotes());

        List<CollectionItem> available = collectionItemRepository.findByAvailableTrue();
        assertEquals(2, available.size());
        assertFalse(available.stream().anyMatch(i -> !i.isAvailable()));
    }
}
