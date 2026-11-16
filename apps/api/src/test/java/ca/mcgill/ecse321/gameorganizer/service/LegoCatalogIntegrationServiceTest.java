package ca.mcgill.ecse321.gameorganizer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import ca.mcgill.ecse321.gameorganizer.dto.response.LegoSetResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ExternalCatalogException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;
import ca.mcgill.ecse321.gameorganizer.services.LegoCatalogIntegrationService;

public class LegoCatalogIntegrationServiceTest {

    private static final String BASE_URL = "https://rebrickable.com/api/v3/lego";
    private static final String API_KEY = "test-api-key";

    private LegoSetRepository legoSetRepository;
    private MockRestServiceServer mockServer;
    private LegoCatalogIntegrationService service;

    @BeforeEach
    public void setUp() {
        legoSetRepository = mock(LegoSetRepository.class);
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        service = new LegoCatalogIntegrationService(builder, legoSetRepository, API_KEY, BASE_URL);
    }

    @Test
    public void testLookupOrImportSetCacheHitSkipsRebrickableCall() {
        LegoSet existing = new LegoSet(
                "71799",
                "NINJAGO City Markets",
                "Ninjago",
                6163,
                2023,
                BigDecimal.valueOf(479.99),
                BigDecimal.valueOf(520.00),
                RetirementStatus.ACTIVE,
                "https://cdn.rebrickable.com/71799.jpg",
                "Modular NINJAGO City market");
        existing.setId(1);

        when(legoSetRepository.findBySetNumber("71799")).thenReturn(Optional.of(existing));

        LegoSetResponseDto result = service.lookupOrImportSet("71799");

        assertNotNull(result);
        assertEquals("71799", result.getSetNumber());
        assertEquals("NINJAGO City Markets", result.getName());
        assertEquals("Ninjago", result.getTheme());
        verify(legoSetRepository, never()).save(any());
        mockServer.verify();
    }

    @Test
    public void testLookupOrImportSetFetchesFromRebrickableAndPersists() {
        when(legoSetRepository.findBySetNumber("10316")).thenReturn(Optional.empty());
        when(legoSetRepository.findBySetNumber("10316-1")).thenReturn(Optional.empty());
        when(legoSetRepository.save(any(LegoSet.class))).thenAnswer(inv -> {
            LegoSet saved = inv.getArgument(0);
            saved.setId(42);
            return saved;
        });

        String setJson = """
                {
                  "set_num": "10316-1",
                  "name": "The Lord of the Rings: Rivendell",
                  "year": 2023,
                  "theme_id": 721,
                  "num_parts": 6167,
                  "set_img_url": "https://cdn.rebrickable.com/media/sets/10316-1.jpg"
                }
                """;
        String themeJson = """
                {
                  "id": 721,
                  "parent_id": null,
                  "name": "Icons"
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/sets/10316-1/"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "key " + API_KEY))
                .andRespond(withSuccess(setJson, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(BASE_URL + "/themes/721/"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "key " + API_KEY))
                .andRespond(withSuccess(themeJson, MediaType.APPLICATION_JSON));

        LegoSetResponseDto result = service.lookupOrImportSet("10316");

        assertNotNull(result);
        assertEquals(42, result.getId());
        assertEquals("10316", result.getSetNumber());
        assertEquals("The Lord of the Rings: Rivendell", result.getName());
        assertEquals("Icons", result.getTheme());
        assertEquals(6167, result.getPieceCount());
        assertEquals(2023, result.getReleaseYear());
        verify(legoSetRepository).save(any(LegoSet.class));
        mockServer.verify();
    }

    @Test
    public void testLookupOrImportSetNotFoundThrowsResourceNotFound() {
        when(legoSetRepository.findBySetNumber("99999")).thenReturn(Optional.empty());
        when(legoSetRepository.findBySetNumber("99999-1")).thenReturn(Optional.empty());

        mockServer.expect(requestTo(BASE_URL + "/sets/99999-1/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThrows(ResourceNotFoundException.class, () -> service.lookupOrImportSet("99999"));
        mockServer.verify();
    }

    @Test
    public void testLookupOrImportSetTimeoutThrowsGatewayTimeout() {
        when(legoSetRepository.findBySetNumber("75192")).thenReturn(Optional.empty());
        when(legoSetRepository.findBySetNumber("75192-1")).thenReturn(Optional.empty());

        mockServer.expect(requestTo(BASE_URL + "/sets/75192-1/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(request -> {
                    throw new SocketTimeoutException("Read timed out");
                });

        ExternalCatalogException ex = assertThrows(
                ExternalCatalogException.class,
                () -> service.lookupOrImportSet("75192"));

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, ex.getStatus());
        mockServer.verify();
    }

    @Test
    public void testLookupOrImportSetMissingApiKeyThrowsServiceUnavailable() {
        RestClient.Builder builder = RestClient.builder();
        LegoCatalogIntegrationService unconfiguredService =
                new LegoCatalogIntegrationService(builder, legoSetRepository, "", BASE_URL);

        when(legoSetRepository.findBySetNumber("10316")).thenReturn(Optional.empty());
        when(legoSetRepository.findBySetNumber("10316-1")).thenReturn(Optional.empty());

        ExternalCatalogException ex = assertThrows(
                ExternalCatalogException.class,
                () -> unconfiguredService.lookupOrImportSet("10316"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
    }
}
