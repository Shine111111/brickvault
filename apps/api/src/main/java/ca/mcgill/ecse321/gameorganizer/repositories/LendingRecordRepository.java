package ca.mcgill.ecse321.gameorganizer.repositories;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.BorrowRequest;
import ca.mcgill.ecse321.gameorganizer.models.LendingRecord;
import ca.mcgill.ecse321.gameorganizer.models.LendingRecord.LendingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LendingRecordRepository extends JpaRepository<LendingRecord, Integer> {

    Optional<LendingRecord> findLendingRecordById(int id);

    List<LendingRecord> findByStatus(LendingStatus status);

    Page<LendingRecord> findByStatus(LendingStatus status, Pageable pageable);

    List<LendingRecord> findByRecordOwner(Account owner);

    Page<LendingRecord> findByRecordOwner(Account owner, Pageable pageable);

    List<LendingRecord> findByStartDateBetween(Date startDate, Date endDate);

    List<LendingRecord> findByEndDateBeforeAndStatus(Date date, LendingStatus status);

    List<LendingRecord> findByRequest_Requester(Account requester);

    Page<LendingRecord> findByRequest_Requester(Account requester, Pageable pageable);

    Optional<LendingRecord> findByRequest(BorrowRequest request);

    List<LendingRecord> findByRequestRequesterEmail(String email);

    @Query("SELECT lr FROM LendingRecord lr WHERE " +
           "(:fromDate IS NULL OR lr.startDate >= :fromDate) AND " +
           "(:toDate IS NULL OR lr.endDate <= :toDate) AND " +
           "(:status IS NULL OR lr.status = :status) AND " +
           "(:borrowerId IS NULL OR lr.request.requester.id = :borrowerId) AND " +
           "(:setId IS NULL OR lr.request.requestedSet.id = :setId)")
    List<LendingRecord> filterLendingRecords(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("status") LendingStatus status,
            @Param("borrowerId") Integer borrowerId,
            @Param("setId") Integer setId);

    @Query("SELECT lr FROM LendingRecord lr WHERE " +
           "(:fromDate IS NULL OR lr.startDate >= :fromDate) AND " +
           "(:toDate IS NULL OR lr.endDate <= :toDate) AND " +
           "(:status IS NULL OR lr.status = :status) AND " +
           "(:borrowerId IS NULL OR lr.request.requester.id = :borrowerId) AND " +
           "(:setId IS NULL OR lr.request.requestedSet.id = :setId)")
    Page<LendingRecord> filterLendingRecords(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("status") LendingStatus status,
            @Param("borrowerId") Integer borrowerId,
            @Param("setId") Integer setId,
            Pageable pageable);
}
