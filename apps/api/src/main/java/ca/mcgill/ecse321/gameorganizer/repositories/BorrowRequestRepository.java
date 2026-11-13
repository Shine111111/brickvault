package ca.mcgill.ecse321.gameorganizer.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.BorrowRequest;
import ca.mcgill.ecse321.gameorganizer.models.BorrowRequestStatus;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {

    Optional<BorrowRequest> findBorrowRequestById(int id);

    List<BorrowRequest> findBorrowRequestsByRequesterName(String username);

    List<BorrowRequest> findBorrowRequestsByRequesterEmail(String email);

    List<BorrowRequest> findByRequester(Account requester);

    List<BorrowRequest> findByRequestedSet(LegoSet requestedSet);

    List<BorrowRequest> findByCollectionItem_OwnerAndStatus(Account owner, BorrowRequestStatus status);

    @Query("SELECT br FROM BorrowRequest br WHERE br.collectionItem.owner.id = :ownerId")
    List<BorrowRequest> findBorrowRequestsByOwnerId(@Param("ownerId") int ownerId);

    @Query("SELECT br FROM BorrowRequest br WHERE br.collectionItem.id = :collectionItemId")
    List<BorrowRequest> findBorrowRequestsByCollectionItemId(@Param("collectionItemId") int collectionItemId);

    @Query("SELECT br FROM BorrowRequest br " +
           "WHERE br.requestedSet.id = :setId " +
           "AND br.status = 'APPROVED' " +
           "AND br.startDate < :endDate " +
           "AND br.endDate > :startDate")
    List<BorrowRequest> findOverlappingApprovedRequests(
            @Param("setId") int setId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT br FROM BorrowRequest br " +
           "WHERE br.collectionItem.id = :collectionItemId " +
           "AND br.status = 'APPROVED' " +
           "AND ((br.startDate <= :endDate) AND (br.endDate >= :startDate))")
    List<BorrowRequest> findOverlappingApprovedRequestsForCollectionItem(
            @Param("collectionItemId") int collectionItemId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
