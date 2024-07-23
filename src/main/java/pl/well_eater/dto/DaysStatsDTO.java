package pl.well_eater.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DaysStatsDTO {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private MealsStatsDTO stats;
}
