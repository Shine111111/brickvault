package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.SetPriceHistory;

@Repository
public interface SetPriceHistoryRepository extends JpaRepository<SetPriceHistory, Integer> {

    List<SetPriceHistory> findByLegoSetIdOrderByRecordedAtAsc(int legoSetId);
}
