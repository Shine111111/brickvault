package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.util.Date;

import ca.mcgill.ecse321.gameorganizer.models.Account;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import lombok.Data;

@Data
public class CreateEventRequest {
    private String title;
    private Date dateTime;
    private String location;
    private String description;
    private int maxParticipants;
    private LegoSet featuredSet;
    private Account host;
    private Integer collectionItemId;
}
