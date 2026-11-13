package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItemResponseDto {
    private int id;
    private LegoSetSummaryDto legoSet;
    private UserSummaryDto owner;
    private ItemCondition condition;
    private BigDecimal purchasePriceCad;
    private LocalDate purchaseDate;
    private boolean forTradeOrSale;
    private boolean available;
    private String notes;

    public CollectionItemResponseDto(CollectionItem item) {
        this.id = item.getId();
        if (item.getLegoSet() != null) {
            this.legoSet = new LegoSetSummaryDto(item.getLegoSet());
        }
        if (item.getOwner() != null) {
            this.owner = new UserSummaryDto(
                    item.getOwner().getId(),
                    item.getOwner().getName(),
                    item.getOwner().getEmail());
        }
        this.condition = item.getCondition();
        this.purchasePriceCad = item.getPurchasePriceCad();
        this.purchaseDate = item.getPurchaseDate();
        this.forTradeOrSale = item.isForTradeOrSale();
        this.available = item.isAvailable();
        this.notes = item.getNotes();
    }
}
