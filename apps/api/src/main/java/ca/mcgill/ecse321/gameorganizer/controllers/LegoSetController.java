package ca.mcgill.ecse321.gameorganizer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetCreationDto;
import ca.mcgill.ecse321.gameorganizer.dto.request.LegoSetSearchCriteria;
import ca.mcgill.ecse321.gameorganizer.dto.response.LegoSetResponseDto;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.services.LegoSetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sets")
public class LegoSetController {

    private final LegoSetService legoSetService;

    @Autowired
    public LegoSetController(LegoSetService legoSetService) {
        this.legoSetService = legoSetService;
    }

    @GetMapping
    public ResponseEntity<List<LegoSetResponseDto>> getAllSets(
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) RetirementStatus retirementStatus,
            @RequestParam(required = false) String setNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPieces,
            @RequestParam(required = false) Integer maxPieces) {
        LegoSetSearchCriteria criteria = new LegoSetSearchCriteria(
                theme, retirementStatus, setNumber, name, minPieces, maxPieces);
        return ResponseEntity.ok(legoSetService.searchLegoSets(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegoSetResponseDto> getSetById(@PathVariable int id) {
        return ResponseEntity.ok(legoSetService.getLegoSetById(id));
    }

    @GetMapping("/number/{setNumber}")
    public ResponseEntity<LegoSetResponseDto> getSetByNumber(@PathVariable String setNumber) {
        return ResponseEntity.ok(legoSetService.getLegoSetBySetNumber(setNumber));
    }

    @PostMapping
    public ResponseEntity<LegoSetResponseDto> createSet(@Valid @RequestBody LegoSetCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(legoSetService.createLegoSet(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegoSetResponseDto> updateSet(
            @PathVariable int id,
            @RequestBody LegoSetCreationDto dto) {
        return ResponseEntity.ok(legoSetService.updateLegoSet(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSet(@PathVariable int id) {
        legoSetService.deleteLegoSet(id);
        return ResponseEntity.noContent().build();
    }
}
