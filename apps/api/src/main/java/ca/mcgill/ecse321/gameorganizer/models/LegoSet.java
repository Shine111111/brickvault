package ca.mcgill.ecse321.gameorganizer.models;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a LEGO set in the BrickVault catalog.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LegoSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String setNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String theme;

    private int pieceCount;

    private int releaseYear;

    @Column(precision = 10, scale = 2)
    private BigDecimal retailPriceCad;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedResaleValueCad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RetirementStatus retirementStatus = RetirementStatus.ACTIVE;

    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "legoSet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("legoSet")
    private Set<Review> reviews = new HashSet<>();

    public LegoSet(
            String setNumber,
            String name,
            String theme,
            int pieceCount,
            int releaseYear,
            BigDecimal retailPriceCad,
            BigDecimal estimatedResaleValueCad,
            RetirementStatus retirementStatus,
            String imageUrl,
            String description) {
        this.setNumber = setNumber;
        this.name = name;
        this.theme = theme;
        this.pieceCount = pieceCount;
        this.releaseYear = releaseYear;
        this.retailPriceCad = retailPriceCad;
        this.estimatedResaleValueCad = estimatedResaleValueCad;
        this.retirementStatus = retirementStatus != null ? retirementStatus : RetirementStatus.ACTIVE;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
