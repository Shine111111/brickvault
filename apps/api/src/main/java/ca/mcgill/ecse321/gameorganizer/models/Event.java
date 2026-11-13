package ca.mcgill.ecse321.gameorganizer.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date; // Changed from java.sql.Date
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Represents a gaming event in the system.
 * Events are organized gatherings where users can meet to play games.
 * Each event has a featured game, a host, and can accommodate a maximum number of participants.
 * 
 * @author @Yessine-glitch
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event {

    /**
     * Unique identifier for the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The title or name of the event.
     */
    private String title;

    /**
     * The date and time when the event will take place.
     * Use java.util.Date or java.time.Instant/OffsetDateTime for timestamp.
     */
    @Temporal(TemporalType.TIMESTAMP) // Ensure DB stores timestamp
    private Date dateTime; // Changed from java.sql.Date

    /**
     * The physical location where the event will be held.
     */
    private String location;

    /**
     * A detailed description of the event.
     */
    @Column(length = 1000) // Allow longer descriptions
    private String description;

    /**
     * The current number of participants attending the event.
     */
    private int currentNumberParticipants;

    /**
     * The maximum number of participants that can attend the event.
     */
    private int maxParticipants;

    /**
     * The main LEGO set that will be featured at this event.
     */
    @ManyToOne
    private LegoSet featuredSet;

    /**
     * The account of the user who is hosting the event.
     */
    @ManyToOne
    @JsonIgnoreProperties({"password", "resetPasswordToken", "resetPasswordTokenExpiry"})
    private Account host;

    /**
     * Optional specific collection item being used for the event.
     */
    @ManyToOne(optional = true)
    private CollectionItem collectionItem;

    public Event(String aTitle, Date aDateTime, String aLocation, String aDescription, int aMaxParticipants, LegoSet aFeaturedSet) {
        title = aTitle;
        dateTime = aDateTime;
        location = aLocation;
        description = aDescription;
        maxParticipants = aMaxParticipants;
        featuredSet = aFeaturedSet;
        currentNumberParticipants = 0;
    }

    public Event(String aTitle, Date aDateTime, String aLocation, String aDescription, int aMaxParticipants, LegoSet aFeaturedSet, Account aHost) {
        title = aTitle;
        dateTime = aDateTime;
        location = aLocation;
        description = aDescription;
        maxParticipants = aMaxParticipants;
        featuredSet = aFeaturedSet;
        this.host = aHost;
        currentNumberParticipants = 0;
    }

    /**
     * Returns a string representation of the Event.
     *
     * @return A string containing the event's details including ID, title, location, description,
     *         maximum participants, date/time, and featured game reference
     */
    public String toString() {
        return super.toString() + "[" +
            "id" + ":" + getId() + "," +
            "title" + ":" + getTitle() + "," +
            "location" + ":" + getLocation() + "," +
            "description" + ":" + getDescription() + "," +
            "maxParticipants" + ":" + getMaxParticipants() + "," +
            "currentNumberParticipants" + ":" + getCurrentNumberParticipants() + "]" + System.getProperties().getProperty("line.separator") +
            "  " + "dateTime" + "=" + (getDateTime() != null ? !getDateTime().equals(this) ? getDateTime().toString().replaceAll("  ", "    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "featuredSet = " + (getFeaturedSet() != null ? Integer.toHexString(System.identityHashCode(getFeaturedSet())) : "null");
    }
}
