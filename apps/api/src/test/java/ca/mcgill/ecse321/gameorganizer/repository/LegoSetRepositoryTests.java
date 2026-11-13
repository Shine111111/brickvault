package ca.mcgill.ecse321.gameorganizer.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@DataJpaTest
@ActiveProfiles("test")
public class LegoSetRepositoryTests {

    @Autowired
    private LegoSetRepository legoSetRepository;

    @BeforeEach
    void setUp() {
        legoSetRepository.deleteAll();
        legoSetRepository.save(new LegoSet(
                "71799", "NINJAGO City Markets", "Ninjago", 6163, 2023,
                new BigDecimal("479.99"), new BigDecimal("520.00"),
                RetirementStatus.ACTIVE, null, "Modular market"));
        legoSetRepository.save(new LegoSet(
                "71719", "Zane's Mino Creature", "Ninjago", 616, 2020,
                new BigDecimal("69.99"), new BigDecimal("95.00"),
                RetirementStatus.RETIRED, null, "Mino creature"));
        legoSetRepository.save(new LegoSet(
                "42143", "Ferrari Daytona SP3", "Technic", 3778, 2022,
                new BigDecimal("559.99"), new BigDecimal("580.00"),
                RetirementStatus.RETIRING_SOON, null, "1:8 supercar"));
    }

    @Test
    void testFindBySetNumber() {
        Optional<LegoSet> found = legoSetRepository.findBySetNumber("71799");
        assertTrue(found.isPresent());
        assertEquals("NINJAGO City Markets", found.get().getName());
    }

    @Test
    void testFilterByThemeAndRetirementStatus() {
        List<LegoSet> ninjago = legoSetRepository.findByThemeIgnoreCase("ninjago");
        assertEquals(2, ninjago.size());

        List<LegoSet> retired = legoSetRepository.findByRetirementStatus(RetirementStatus.RETIRED);
        assertEquals(1, retired.size());
        assertEquals("71719", retired.get(0).getSetNumber());
    }

    @Test
    void testSearchLegoSetsWithCombinedFilters() {
        List<LegoSet> results = legoSetRepository.searchLegoSets(
                "Ninjago", RetirementStatus.ACTIVE, "717", null, 1000, null);
        assertEquals(1, results.size());
        assertEquals("71799", results.get(0).getSetNumber());
    }
}
