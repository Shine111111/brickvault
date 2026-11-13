package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowRequestDto {
    private int requesterId;
    private int requestedSetId;
    private int collectionItemId;
    private Date startDate;
    private Date endDate;
}
