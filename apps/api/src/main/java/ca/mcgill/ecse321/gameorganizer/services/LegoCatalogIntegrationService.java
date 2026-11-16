package ca.mcgill.ecse321.gameorganizer.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.mcgill.ecse321.gameorganizer.dto.response.LegoSetResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ExternalCatalogException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@Service
public class LegoCatalogIntegrationService {

    private final RestClient restClient;
    private final LegoSetRepository legoSetRepository;
    private final String apiKey;

    @Autowired
    public LegoCatalogIntegrationService(
            RestClient.Builder restClientBuilder,
            LegoSetRepository legoSetRepository,
            @Value("${rebrickable.api.key:}") String apiKey,
            @Value("${rebrickable.api.base-url:https://rebrickable.com/api/v3/lego}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.legoSetRepository = legoSetRepository;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    @Transactional
    public LegoSetResponseDto lookupOrImportSet(String rawSetNumber) {
        if (rawSetNumber == null || rawSetNumber.isBlank()) {
            throw new IllegalArgumentException("Set number must not be blank");
        }

        String trimmed = rawSetNumber.trim();
        String normalized = normalizeSetNumber(trimmed);

        Optional<LegoSet> cached = findInCache(trimmed, normalized);
        if (cached.isPresent()) {
            return new LegoSetResponseDto(cached.get());
        }

        if (apiKey.isEmpty()) {
            throw new ExternalCatalogException(
                    "Rebrickable API key is not configured",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        RebrickableSetResponse remoteSet = fetchSetFromRebrickable(normalized);
        String themeName = resolveThemeName(remoteSet.themeId());

        LegoSet entity = new LegoSet(
                trimmed,
                remoteSet.name(),
                themeName,
                Math.max(1, remoteSet.numParts()),
                Math.max(1949, remoteSet.year()),
                null,
                null,
                RetirementStatus.ACTIVE,
                remoteSet.setImgUrl(),
                "Imported from Rebrickable (" + normalized + ")");

        return new LegoSetResponseDto(legoSetRepository.save(entity));
    }

    public String normalizeSetNumber(String setNumber) {
        String trimmed = setNumber.trim();
        return trimmed.contains("-") ? trimmed : trimmed + "-1";
    }

    private Optional<LegoSet> findInCache(String trimmed, String normalized) {
        Optional<LegoSet> exact = legoSetRepository.findBySetNumber(trimmed);
        if (exact.isPresent()) {
            return exact;
        }
        Optional<LegoSet> byNormalized = legoSetRepository.findBySetNumber(normalized);
        if (byNormalized.isPresent()) {
            return byNormalized;
        }
        if (trimmed.endsWith("-1")) {
            return legoSetRepository.findBySetNumber(trimmed.substring(0, trimmed.length() - 2));
        }
        return Optional.empty();
    }

    private RebrickableSetResponse fetchSetFromRebrickable(String normalizedSetNumber) {
        try {
            RebrickableSetResponse response = restClient.get()
                    .uri("/sets/{set_num}/", normalizedSetNumber)
                    .header(HttpHeaders.AUTHORIZATION, "key " + apiKey)
                    .retrieve()
                    .body(RebrickableSetResponse.class);
            if (response == null || response.name() == null) {
                throw new ResourceNotFoundException(
                        "LEGO set not found in Rebrickable catalog: " + normalizedSetNumber);
            }
            return response;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "LEGO set not found in Rebrickable catalog: " + normalizedSetNumber);
        } catch (ResourceAccessException ex) {
            throw new ExternalCatalogException(
                    "Timed out connecting to Rebrickable API",
                    HttpStatus.GATEWAY_TIMEOUT,
                    ex);
        } catch (RestClientException ex) {
            throw new ExternalCatalogException(
                    "Failed to fetch set from Rebrickable API: " + ex.getMessage(),
                    HttpStatus.BAD_GATEWAY,
                    ex);
        }
    }

    private String resolveThemeName(Integer themeId) {
        if (themeId == null) {
            return "LEGO";
        }
        try {
            RebrickableThemeResponse theme = restClient.get()
                    .uri("/themes/{id}/", themeId)
                    .header(HttpHeaders.AUTHORIZATION, "key " + apiKey)
                    .retrieve()
                    .body(RebrickableThemeResponse.class);
            if (theme != null && theme.name() != null && !theme.name().isBlank()) {
                return theme.name();
            }
        } catch (RestClientException ignored) {
            // Fall back to default theme label if theme endpoint lookup fails
        }
        return "LEGO";
    }

    public record RebrickableSetResponse(
            @JsonProperty("set_num") String setNum,
            String name,
            int year,
            @JsonProperty("theme_id") Integer themeId,
            @JsonProperty("num_parts") int numParts,
            @JsonProperty("set_img_url") String setImgUrl) {
    }

    public record RebrickableThemeResponse(
            int id,
            @JsonProperty("parent_id") Integer parentId,
            String name) {
    }
}
