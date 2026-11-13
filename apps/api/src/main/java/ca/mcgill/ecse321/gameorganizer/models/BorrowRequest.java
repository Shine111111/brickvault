package ca.mcgill.ecse321.gameorganizer.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Represents a trade/loan request for a LEGO collection item from its owner.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Date startDate;

    private Date endDate;

    @Enumerated(EnumType.STRING)
    private BorrowRequestStatus status;

    private Date requestDate;

    @ManyToOne
    private LegoSet requestedSet;

    @ManyToOne
    private CollectionItem collectionItem;

    @ManyToOne
    private Account requester;

    @ManyToOne(optional = true)
    private Account responder;

    public BorrowRequest(
            Date aStartDate,
            Date aEndDate,
            BorrowRequestStatus aStatus,
            Date aRequestDate,
            LegoSet aRequestedSet,
            CollectionItem aCollectionItem) {
        startDate = aStartDate;
        endDate = aEndDate;
        status = aStatus;
        requestDate = aRequestDate;
        requestedSet = aRequestedSet;
        collectionItem = aCollectionItem;
    }

    @Override
    public String toString() {
        return "BorrowRequest[id=" + id + ", status=" + status + ", startDate=" + startDate
                + ", endDate=" + endDate + ", requestDate=" + requestDate + "]";
    }
}
