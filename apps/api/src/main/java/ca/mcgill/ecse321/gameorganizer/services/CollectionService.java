package ca.mcgill.ecse321.gameorganizer.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.gameorganizer.dto.request.CollectionItemRequestDto;
import ca.mcgill.ecse321.gameorganizer.dto.response.CollectionItemResponseDto;
import ca.mcgill.ecse321.gameorganizer.exceptions.ForbiddenException;
import ca.mcgill.ecse321.gameorganizer.exceptions.ResourceNotFoundException;
import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.repositories.AccountRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.CollectionItemRepository;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@Service
public class CollectionService {

    private final CollectionItemRepository collectionItemRepository;
    private final LegoSetRepository legoSetRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CollectionService(
            CollectionItemRepository collectionItemRepository,
            LegoSetRepository legoSetRepository,
            AccountRepository accountRepository) {
        this.collectionItemRepository = collectionItemRepository;
        this.legoSetRepository = legoSetRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<CollectionItemResponseDto> getCollectionByOwnerEmail(String email) {
        return collectionItemRepository.findByOwnerEmail(email).stream()
                .map(CollectionItemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollectionItemResponseDto> getCollectionByOwnerId(int ownerId) {
        return collectionItemRepository.findByOwnerId(ownerId).stream()
                .map(CollectionItemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollectionItemResponseDto> getItemsForTradeOrSale() {
        return collectionItemRepository.findByForTradeOrSaleTrue().stream()
                .map(CollectionItemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollectionItemResponseDto> getItemsByLegoSetId(int legoSetId) {
        return collectionItemRepository.findByLegoSetId(legoSetId).stream()
                .map(CollectionItemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CollectionItemResponseDto getCollectionItemById(int id) {
        CollectionItem item = collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection item not found with id: " + id));
        return new CollectionItemResponseDto(item);
    }

    @Transactional
    public CollectionItemResponseDto addCollectionItem(String ownerEmail, CollectionItemRequestDto dto) {
        Account owner = accountRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + ownerEmail));
        LegoSet legoSet = legoSetRepository.findById(dto.getLegoSetId())
                .orElseThrow(() -> new ResourceNotFoundException("LEGO set not found with id: " + dto.getLegoSetId()));

        CollectionItem item = new CollectionItem(
                legoSet,
                owner,
                dto.getCondition() != null ? dto.getCondition() : ItemCondition.BUILT_DISPLAY,
                dto.getPurchasePriceCad(),
                dto.getPurchaseDate(),
                dto.isForTradeOrSale(),
                dto.isAvailable(),
                dto.getNotes());
        return new CollectionItemResponseDto(collectionItemRepository.save(item));
    }

    @Transactional
    public CollectionItemResponseDto updateCollectionItem(int id, String requesterEmail, CollectionItemRequestDto dto) {
        CollectionItem item = collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection item not found with id: " + id));
        if (!item.getOwner().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ForbiddenException("You do not own this collection item");
        }
        if (dto.getCondition() != null) item.setCondition(dto.getCondition());
        if (dto.getPurchasePriceCad() != null) item.setPurchasePriceCad(dto.getPurchasePriceCad());
        if (dto.getPurchaseDate() != null) item.setPurchaseDate(dto.getPurchaseDate());
        item.setForTradeOrSale(dto.isForTradeOrSale());
        item.setAvailable(dto.isAvailable());
        if (dto.getNotes() != null) item.setNotes(dto.getNotes());
        return new CollectionItemResponseDto(collectionItemRepository.save(item));
    }

    @Transactional
    public void deleteCollectionItem(int id, String requesterEmail) {
        CollectionItem item = collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection item not found with id: " + id));
        if (!item.getOwner().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ForbiddenException("You do not own this collection item");
        }
        collectionItemRepository.delete(item);
    }
}
