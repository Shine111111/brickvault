package ca.mcgill.ecse321.gameorganizer.dto.response;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.Event;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;

import java.util.Date;
import java.util.UUID;
import lombok.Data;

@Data
public class EventResponse {

    public EventResponse(Event event) {
        this.eventId = event.getId();
        this.title = event.getTitle();
        this.dateTime = event.getDateTime();
        this.location = event.getLocation();
        this.description = event.getDescription();
        this.currentNumberParticipants = event.getCurrentNumberParticipants();
        this.maxParticipants = event.getMaxParticipants();

        if (event.getFeaturedSet() != null) {
            this.featuredSet = new SetDto(event.getFeaturedSet());
        }

        if (event.getHost() != null) {
            this.host = new AccountDto(event.getHost());
        }
    }

    private UUID eventId;
    private String title;
    private Date dateTime;
    private String location;
    private String description;
    private int currentNumberParticipants;
    private int maxParticipants;
    private SetDto featuredSet;
    private AccountDto host;

    @Data
    public static class SetDto {
        private int id;
        private String setNumber;
        private String name;
        private String imageUrl;

        public SetDto(LegoSet set) {
            this.id = set.getId();
            this.setNumber = set.getSetNumber();
            this.name = set.getName();
            this.imageUrl = set.getImageUrl();
        }
    }

    @Data
    public static class AccountDto {
        private int id;
        private String name;
        private String email;

        public AccountDto(Account account) {
            this.id = account.getId();
            this.name = account.getName();
            this.email = account.getEmail();
        }
    }
}
