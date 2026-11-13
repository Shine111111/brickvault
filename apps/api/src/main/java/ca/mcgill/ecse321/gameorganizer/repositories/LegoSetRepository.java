package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;

@Repository
public interface LegoSetRepository extends JpaRepository<LegoSet, Integer> {

    Optional<LegoSet> findBySetNumber(String setNumber);

    boolean existsBySetNumber(String setNumber);

    List<LegoSet> findByThemeIgnoreCase(String theme);

    List<LegoSet> findByRetirementStatus(RetirementStatus retirementStatus);

    List<LegoSet> findByThemeIgnoreCaseAndRetirementStatus(String theme, RetirementStatus retirementStatus);

    List<LegoSet> findByNameContainingIgnoreCase(String name);

    @Query("SELECT ls FROM LegoSet ls WHERE " +
           "(:theme IS NULL OR LOWER(ls.theme) = LOWER(:theme)) AND " +
           "(:retirementStatus IS NULL OR ls.retirementStatus = :retirementStatus) AND " +
           "(:setNumber IS NULL OR LOWER(ls.setNumber) LIKE LOWER(CONCAT('%', :setNumber, '%'))) AND " +
           "(:name IS NULL OR LOWER(ls.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minPieces IS NULL OR ls.pieceCount >= :minPieces) AND " +
           "(:maxPieces IS NULL OR ls.pieceCount <= :maxPieces)")
    List<LegoSet> searchLegoSets(
            @Param("theme") String theme,
            @Param("retirementStatus") RetirementStatus retirementStatus,
            @Param("setNumber") String setNumber,
            @Param("name") String name,
            @Param("minPieces") Integer minPieces,
            @Param("maxPieces") Integer maxPieces);
}
