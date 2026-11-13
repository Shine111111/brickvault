package ca.mcgill.ecse321.gameorganizer.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LendingHistoryFilterDto {
    private Date fromDate;
    private Date toDate;
    private String status;
    private Integer setId;
    private Integer borrowerId;
}
