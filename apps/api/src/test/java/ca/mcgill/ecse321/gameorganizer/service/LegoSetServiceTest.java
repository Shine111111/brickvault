package ca.mcgill.ecse321.gameorganizer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetCreationDto;
import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetSearchCriteria;
import ca.mcgill.ecse321.gameorganizer.dto.response.LegoSetResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.InvalidOperationException;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.EventRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.services.LegoSetService;

@ExtendWith(MockitoExtension.class)
public class LegoSetServiceTest {

    @Mock
    private LegoSetRepository legoSetRepository;

    @Mock
    private EventRepository eventRepository;

    private LegoSetService legoSetService;

    @BeforeEach
    void setUp() {
        legoSetService = new LegoSetService(legoSetRepository, eventRepository);
    }

    @Test
    void testCreateLegoSetSuccess() {
        LegoSetCreationDto dto = new LegoSetCreationDto(
                "71799", "NINJAGO City Markets", "Ninjago", 6163, 2023,
                new BigDecimal("479.99"), new BigDecimal("520.00"),
                RetirementStatus.ACTIVE, null, "Modular market");
        when(legoSetRepository.existsBySetNumber("71799")).thenReturn(false);
        when(legoSetRepository.save(any(LegoSet.class))).thenAnswer(inv -> {
            LegoSet s = inv.getArgument(0);
            s.setId(1);
            return s;
        });

        LegoSetResponseDto created = legoSetService.createLegoSet(dto);
        assertEquals("71799", created.getSetNumber());
        assertEquals("Ninjago", created.getTheme());
    }

    @Test
    void testCreateLegoSetDuplicateSetNumberThrows() {
        LegoSetCreationDto dto = new LegoSetCreationDto(
                "71799", "NINJAGO City Markets", "Ninjago", 6163, 2023,
                new BigDecimal("479.99"), new BigDecimal("520.00"),
                RetirementStatus.ACTIVE, null, "Modular market");
        when(legoSetRepository.existsBySetNumber("71799")).thenReturn(true);

        assertThrows(InvalidOperationException.class, () -> legoSetService.createLegoSet(dto));
    }

    @Test
    void testSearchLegoSetsByTheme() {
        LegoSet set = new LegoSet("21057", "Singapore", "Architecture", 827, 2022,
                new BigDecimal("79.99"), new BigDecimal("85.00"),
                RetirementStatus.ACTIVE, null, "Skyline");
        when(legoSetRepository.searchLegoSets("Architecture", null, null, null, null, null))
                .thenReturn(List.of(set));

        List<LegoSetResponseDto> results = legoSetService.searchLegoSets(
                new LegoSetSearchCriteria("Architecture", null, null, null, null, null));
        assertEquals(1, results.size());
        assertEquals("21057", results.get(0).getSetNumber());
    }

    @Test
    void testGetLegoSetBySetNumber() {
        LegoSet set = new LegoSet("75192", "Millennium Falcon", "Star Wars", 7541, 2017,
                new BigDecimal("1049.99"), new BigDecimal("1120.00"),
                RetirementStatus.ACTIVE, null, "UCS Falcon");
        when(legoSetRepository.findBySetNumber("75192")).thenReturn(Optional.of(set));

        LegoSetResponseDto dto = legoSetService.getLegoSetBySetNumber("75192");
        assertEquals("Millennium Falcon", dto.getName());
    }
}
