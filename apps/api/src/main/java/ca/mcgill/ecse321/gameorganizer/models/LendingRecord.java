package ca.mcgill.ecse321.gameorganizer.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Represents a record of a LEGO set lending/trade transaction between an item owner and a borrower.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class LendingRecord {

    public enum LendingStatus {
        ACTIVE, OVERDUE, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private LendingStatus status;

    @ManyToOne
    private Account recordOwner;

    @OneToOne
    @JoinColumn(name = "request_id", unique = true)
    private BorrowRequest request;

    private boolean isDamaged = false;

    @Column(length = 1000)
    private String damageNotes;

    @Temporal(TemporalType.TIMESTAMP)
    private Date damageAssessmentDate;

    private int damageSeverity = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private Integer lastModifiedBy;

    @Column(length = 500)
    private String statusChangeReason;

    private Integer closedBy;

    @Column(length = 500)
    private String closingReason;

    public LendingRecord(
            Date aStartDate,
            Date aEndDate,
            LendingStatus aStatus,
            BorrowRequest aRequest,
            Account aOwner) {
        if (aStartDate == null || aEndDate == null || aStatus == null || aRequest == null || aOwner == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }
        if (aEndDate.before(aStartDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (aRequest.getCollectionItem() != null
                && aRequest.getCollectionItem().getOwner() != null
                && aRequest.getCollectionItem().getOwner().getId() != aOwner.getId()) {
            throw new IllegalArgumentException("The record owner must be the owner of the collection item in the request");
        }
        startDate = aStartDate;
        endDate = aEndDate;
        status = aStatus;
        request = aRequest;
        recordOwner = aOwner;
        lastModifiedDate = new Date();
        statusChangeReason = "Initial record creation";
    }

    public long getDurationInDays() {
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    public void recordDamage(boolean damaged, String notes, int severity) {
        this.isDamaged = damaged;
        this.damageNotes = notes;
        this.damageAssessmentDate = new Date();
        this.damageSeverity = Math.min(3, Math.max(0, severity));
    }

    public void recordClosing(Integer userId, String reason) {
        this.status = LendingStatus.CLOSED;
        this.endDate = new Date();
        this.closedBy = userId;
        this.closingReason = reason;
        this.lastModifiedDate = new Date();
        this.lastModifiedBy = userId;
        this.statusChangeReason = "Record closed: " + reason;
    }

    @Override
    public String toString() {
        return "LendingRecord[id=" + id + ", status=" + status + ", startDate=" + startDate
                + ", endDate=" + endDate + "]";
    }
}
