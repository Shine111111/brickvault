package ca.mcgill.ecse321.gameorganizer.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.mcgill.ecse321.gameorganizer.TestJwtConfig;
import ca.mcgill.ecse321.gameorganizer.config.TestSecurityConfig;
import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetCreationDto;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestSecurityConfig.class, TestJwtConfig.class })
public class LegoSetIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LegoSetRepository legoSetRepository;

    private LegoSet ninjagoSet;
    private LegoSet retiredSet;

    @BeforeEach
    public void setUp() {
        legoSetRepository.deleteAll();

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
                "Four-story modular NINJAGO City market"));

        retiredSet = legoSetRepository.save(new LegoSet(
                "71719",
                "Zane's Mino Creature",
                "Ninjago",
                616,
                2020,
                BigDecimal.valueOf(69.99),
                BigDecimal.valueOf(95.00),
                RetirementStatus.RETIRED,
                "https://example.com/71719.png",
                "Posable Mino creature"));
    }

    @AfterEach
    public void tearDown() {
        legoSetRepository.deleteAll();
    }

    @Test
    public void testGetSetsAndFilters() throws Exception {
        mockMvc.perform(get("/api/sets")
                        .with(user("collector@brickvault.ca").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/sets").param("theme", "Ninjago").param("retirementStatus", "RETIRED")
                        .with(user("collector@brickvault.ca").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].setNumber").value("71719"));

        mockMvc.perform(get("/api/sets/number/71799")
                        .with(user("collector@brickvault.ca").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NINJAGO City Markets"));
    }

    @Test
    public void testCreateUpdateAndDeleteSet() throws Exception {
        LegoSetCreationDto createDto = new LegoSetCreationDto(
                "21057",
                "Singapore",
                "Architecture",
                827,
                2022,
                BigDecimal.valueOf(79.99),
                BigDecimal.valueOf(85.00),
                RetirementStatus.ACTIVE,
                "https://example.com/21057.png",
                "Singapore skyline");

        String responseBody = mockMvc.perform(post("/api/sets")
                        .with(user("collector@brickvault.ca").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.setNumber").value("21057"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int createdId = objectMapper.readTree(responseBody).get("id").asInt();

        LegoSetCreationDto updateDto = new LegoSetCreationDto(
                "21057",
                "Singapore Skyline",
                "Architecture",
                827,
                2022,
                BigDecimal.valueOf(79.99),
                BigDecimal.valueOf(90.00),
                RetirementStatus.RETIRING_SOON,
                "https://example.com/21057.png",
                "Updated description");

        mockMvc.perform(put("/api/sets/" + createdId)
                        .with(user("collector@brickvault.ca").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Singapore Skyline"))
                .andExpect(jsonPath("$.retirementStatus").value("RETIRING_SOON"));

        mockMvc.perform(delete("/api/sets/" + createdId)
                        .with(user("collector@brickvault.ca").roles("USER")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/sets/" + createdId)
                        .with(user("collector@brickvault.ca").roles("USER")))
                .andExpect(status().isNotFound());
    }
}
