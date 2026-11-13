package ca.mcgill.ecse321.gameorganizer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.gameorganizer.dto.request.CollectionItemRequestDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.CollectionItemResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.UnauthedException;
import ca.mcgill.ecse321.gameorganizer.middleware.RequireItemOwner;
import ca.mcgill.ecse321.gameorganizer.services.CollectionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<CollectionItemResponseDto>> getCollection(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(required = false) Integer legoSetId,
            @RequestParam(required = false) Boolean forTradeOrSale) {
        if (ownerId != null) {
            return ResponseEntity.ok(collectionService.getCollectionByOwnerId(ownerId));
        }
        if (legoSetId != null) {
            return ResponseEntity.ok(collectionService.getItemsByLegoSetId(legoSetId));
        }
        if (Boolean.TRUE.equals(forTradeOrSale)) {
            return ResponseEntity.ok(collectionService.getItemsForTradeOrSale());
        }
        return ResponseEntity.ok(collectionService.getCollectionByOwnerEmail(requireAuthenticatedEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionItemResponseDto> getCollectionItem(@PathVariable int id) {
        return ResponseEntity.ok(collectionService.getCollectionItemById(id));
    }

    @PostMapping
    public ResponseEntity<CollectionItemResponseDto> addCollectionItem(
            @Valid @RequestBody CollectionItemRequestDto dto) {
        String email = requireAuthenticatedEmail();
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.addCollectionItem(email, dto));
    }

    @RequireItemOwner
    @PutMapping("/{id}")
    public ResponseEntity<CollectionItemResponseDto> updateCollectionItem(
            @PathVariable int id,
            @RequestBody CollectionItemRequestDto dto) {
        String email = requireAuthenticatedEmail();
        return ResponseEntity.ok(collectionService.updateCollectionItem(id, email, dto));
    }

    @RequireItemOwner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollectionItem(@PathVariable int id) {
        String email = requireAuthenticatedEmail();
        collectionService.deleteCollectionItem(id, email);
        return ResponseEntity.noContent().build();
    }

    private String requireAuthenticatedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthedException("Authentication required");
        }
        return auth.getName();
    }
}
