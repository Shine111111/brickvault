package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestDto {
    private int id;
    private int requesterId;
    private int requestedSetId;
    private int collectionItemId;
    private Date startDate;
    private Date endDate;
    private String status;
    private Date requestDate;
}
