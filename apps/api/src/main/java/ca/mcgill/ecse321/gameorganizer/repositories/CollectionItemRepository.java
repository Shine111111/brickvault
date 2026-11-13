package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.CollectionItem;
import ca.mcgill.ecse321.gameorganizer.models.ItemCondition;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Integer> {

    List<CollectionItem> findByOwner(Account owner);

    List<CollectionItem> findByOwnerId(int ownerId);

    List<CollectionItem> findByOwnerEmail(String email);

    List<CollectionItem> findByLegoSet(LegoSet legoSet);

    List<CollectionItem> findByLegoSetId(int legoSetId);

    List<CollectionItem> findByOwnerIdAndLegoSetId(int ownerId, int legoSetId);

    @Query("SELECT ci FROM CollectionItem ci WHERE ci.forTradeOrSale = true")
    List<CollectionItem> findByForTradeOrSaleTrue();

    List<CollectionItem> findByAvailableTrue();

    List<CollectionItem> findByCondition(ItemCondition condition);
}
