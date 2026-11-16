package ca.mcgill.ecse321.gameorganizer.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeBreakdownDto {
    private String theme;
    private int setCount;
    private int pieceCount;
    private BigDecimal totalValueCad;
}
