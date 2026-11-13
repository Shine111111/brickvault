package ca.mcgill.ecse321.gameorganizer.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetCreationDto;
import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetSearchCriteria;
import ca.mcgill.ecse321.gameorganizer.dto.response.LegoSetResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.InvalidOperationException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.EventRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@Service
public class LegoSetService {

    private final LegoSetRepository legoSetRepository;
    private final EventRepository eventRepository;

    @Autowired
    public LegoSetService(LegoSetRepository legoSetRepository, EventRepository eventRepository) {
        this.legoSetRepository = legoSetRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<LegoSetResponseDto> searchLegoSets(LegoSetSearchCriteria criteria) {
        if (criteria == null) {
            return legoSetRepository.findAll().stream()
                    .map(LegoSetResponseDto::new)
                    .collect(Collectors.toList());
        }
        String theme = (criteria.getTheme() != null && !criteria.getTheme().isBlank()) ? criteria.getTheme().trim() : null;
        String setNumber = (criteria.getSetNumber() != null && !criteria.getSetNumber().isBlank()) ? criteria.getSetNumber().trim() : null;
        String name = (criteria.getName() != null && !criteria.getName().isBlank()) ? criteria.getName().trim() : null;

        return legoSetRepository.searchLegoSets(
                        theme,
                        criteria.getRetirementStatus(),
                        setNumber,
                        name,
                        criteria.getMinPieces(),
                        criteria.getMaxPieces())
                .stream()
                .map(LegoSetResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LegoSetResponseDto getLegoSetById(int id) {
        LegoSet set = legoSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LEGO set not found with id: " + id));
        return new LegoSetResponseDto(set);
    }

    @Transactional(readOnly = true)
    public LegoSetResponseDto getLegoSetBySetNumber(String setNumber) {
        LegoSet set = legoSetRepository.findBySetNumber(setNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LEGO set not found with setNumber: " + setNumber));
        return new LegoSetResponseDto(set);
    }

    @Transactional
    public LegoSetResponseDto createLegoSet(LegoSetCreationDto dto) {
        if (dto == null || dto.getSetNumber() == null || dto.getSetNumber().isBlank()) {
            throw new IllegalArgumentException("Set number is required");
        }
        if (legoSetRepository.existsBySetNumber(dto.getSetNumber().trim())) {
            throw new InvalidOperationException("A LEGO set with setNumber " + dto.getSetNumber() + " already exists");
        }
        LegoSet set = new LegoSet(
                dto.getSetNumber().trim(),
                dto.getName(),
                dto.getTheme(),
                dto.getPieceCount(),
                dto.getReleaseYear(),
                dto.getRetailPriceCad(),
                dto.getEstimatedResaleValueCad(),
                dto.getRetirementStatus() != null ? dto.getRetirementStatus() : RetirementStatus.ACTIVE,
                dto.getImageUrl(),
                dto.getDescription());
        return new LegoSetResponseDto(legoSetRepository.save(set));
    }

    @Transactional
    public LegoSetResponseDto updateLegoSet(int id, LegoSetCreationDto dto) {
        LegoSet set = legoSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LEGO set not found with id: " + id));
        if (dto.getSetNumber() != null && !dto.getSetNumber().isBlank()) {
            String newNumber = dto.getSetNumber().trim();
            if (!newNumber.equals(set.getSetNumber()) && legoSetRepository.existsBySetNumber(newNumber)) {
                throw new InvalidOperationException("Set number already in use: " + newNumber);
            }
            set.setSetNumber(newNumber);
        }
        if (dto.getName() != null) set.setName(dto.getName());
        if (dto.getTheme() != null) set.setTheme(dto.getTheme());
        if (dto.getPieceCount() > 0) set.setPieceCount(dto.getPieceCount());
        if (dto.getReleaseYear() >= 1949) set.setReleaseYear(dto.getReleaseYear());
        if (dto.getRetailPriceCad() != null) set.setRetailPriceCad(dto.getRetailPriceCad());
        if (dto.getEstimatedResaleValueCad() != null) set.setEstimatedResaleValueCad(dto.getEstimatedResaleValueCad());
        if (dto.getRetirementStatus() != null) set.setRetirementStatus(dto.getRetirementStatus());
        if (dto.getImageUrl() != null) set.setImageUrl(dto.getImageUrl());
        if (dto.getDescription() != null) set.setDescription(dto.getDescription());
        return new LegoSetResponseDto(legoSetRepository.save(set));
    }

    @Transactional
    public void deleteLegoSet(int id) {
        if (!legoSetRepository.existsById(id)) {
            throw new ResourceNotFoundException("LEGO set not found with id: " + id);
        }
        eventRepository.deleteAllByFeaturedSetId(id);
        legoSetRepository.deleteById(id);
    }
}
