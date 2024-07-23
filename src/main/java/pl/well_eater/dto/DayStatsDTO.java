package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DayStatsDTO {
    private LocalDate date;
    private MealsStatsDTO stats;
}
