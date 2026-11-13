package ca.mcgill.ecse321.gameorganizer.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import ca.mcgill.ecse321.gameorganizer.dto.request.CollectionItemRequestDto;
import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.AccountRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestSecurityConfig.class, TestJwtConfig.class })
public class CollectionIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LegoSetRepository legoSetRepository;

    @Autowired
    private CollectionItemRepository collectionItemRepository;

    private Account owner;
    private Account otherUser;
    private LegoSet legoSet;

    @BeforeEach
    public void setUp() {
        collectionItemRepository.deleteAll();
        legoSetRepository.deleteAll();
        accountRepository.deleteAll();

        owner = accountRepository.save(new Account("Owner", "owner@brickvault.ca", "password123"));
        otherUser = accountRepository.save(new Account("Other", "other@brickvault.ca", "password123"));

        legoSet = legoSetRepository.save(new LegoSet(
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
    }

    @AfterEach
    public void tearDown() {
        collectionItemRepository.deleteAll();
        legoSetRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    public void testCollectionCrudAndOwnershipEnforcement() throws Exception {
        CollectionItemRequestDto createDto = new CollectionItemRequestDto(
                legoSet.getId(),
                ItemCondition.SEALED_NISB,
                BigDecimal.valueOf(450.00),
                LocalDate.of(2024, 1, 15),
                true,
                true,
                "Mint NISB");

        String createdJson = mockMvc.perform(post("/api/collection")
                        .with(user(owner.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.condition").value("SEALED_NISB"))
                .andExpect(jsonPath("$.forTradeOrSale").value(true))
                .andExpect(jsonPath("$.legoSet.setNumber").value("71799"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int itemId = objectMapper.readTree(createdJson).get("id").asInt();

        mockMvc.perform(get("/api/collection")
                        .with(user(owner.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/collection").param("forTradeOrSale", "true")
                        .with(user(owner.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        CollectionItemRequestDto updateDto = new CollectionItemRequestDto(
                legoSet.getId(),
                ItemCondition.BUILT_DISPLAY,
                BigDecimal.valueOf(450.00),
                LocalDate.of(2024, 1, 15),
                false,
                true,
                "Built and displayed");

        mockMvc.perform(put("/api/collection/" + itemId)
                        .with(user(otherUser.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/collection/" + itemId)
                        .with(user(owner.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition").value("BUILT_DISPLAY"))
                .andExpect(jsonPath("$.forTradeOrSale").value(false));

        mockMvc.perform(delete("/api/collection/" + itemId)
                        .with(user(owner.getEmail()).roles("USER")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/collection/" + itemId)
                        .with(user(owner.getEmail()).roles("USER")))
                .andExpect(status().isNotFound());
    }
}
