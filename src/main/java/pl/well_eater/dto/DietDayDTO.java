package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DietDayDTO {
    private Long dietDayId;
    private LocalDate date;
    private String username;
    private Set<DietMealDTO> meals = new HashSet<>();
    private DayStatsDTO stats;
}
