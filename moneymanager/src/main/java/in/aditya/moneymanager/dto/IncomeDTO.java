package in.aditya.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class IncomeDTO {

    private Long id;
    private Long category_id;
    private Long profile_id;
    private String name;
    private BigDecimal amount;
    private String categoryName;
    private String icon;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
