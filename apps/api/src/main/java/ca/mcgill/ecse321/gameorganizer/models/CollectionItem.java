package ca.mcgill.ecse321.gameorganizer.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a physical LEGO set copy owned by an Account in their BrickVault collection.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(optional = false)
    private LegoSet legoSet;

    @ManyToOne(optional = false)
    private Account owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCondition condition = ItemCondition.BUILT_DISPLAY;

    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePriceCad;

    private LocalDate purchaseDate;

    private boolean forTradeOrSale = false;

    private boolean available = true;

    @Column(length = 500)
    private String notes;

    public CollectionItem(
            LegoSet legoSet,
            Account owner,
            ItemCondition condition,
            BigDecimal purchasePriceCad,
            LocalDate purchaseDate,
            boolean forTradeOrSale,
            boolean available,
            String notes) {
        this.legoSet = legoSet;
        this.owner = owner;
        this.condition = condition != null ? condition : ItemCondition.BUILT_DISPLAY;
        this.purchasePriceCad = purchasePriceCad;
        this.purchaseDate = purchaseDate;
        this.forTradeOrSale = forTradeOrSale;
        this.available = available;
        this.notes = notes;
    }
}
