package ca.mcgill.ecse321.gameorganizer.models;

import java.util.Date;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a review for a LEGO set in BrickVault.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int rating;

    @Column(length = 1000)
    private String comment;

    private Date dateSubmitted;

    @ManyToOne
    @JoinColumn(name = "lego_set_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private LegoSet legoSet;

    @ManyToOne
    private Account reviewer;

    public Review(int aRating, String aComment, Date aDateSubmitted) {
        rating = aRating;
        comment = aComment;
        dateSubmitted = aDateSubmitted;
    }

    @Override
    public String toString() {
        return "Review[id=" + id + ", rating=" + rating + ", comment=" + comment
                + ", dateSubmitted=" + dateSubmitted + "]";
    }
}
