package ca.mcgill.ecse321.gameorganizer.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SetPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(optional = false)
    private LegoSet legoSet;

    @Column(nullable = false)
    private LocalDate recordedAt;

    @Column(precision = 10, scale = 2)
    private BigDecimal retailPriceCad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedMarketValueCad;

    public SetPriceHistory(
            LegoSet legoSet,
            LocalDate recordedAt,
            BigDecimal retailPriceCad,
            BigDecimal estimatedMarketValueCad) {
        this.legoSet = legoSet;
        this.recordedAt = recordedAt;
        this.retailPriceCad = retailPriceCad;
        this.estimatedMarketValueCad = estimatedMarketValueCad;
    }
}
