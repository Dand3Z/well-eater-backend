package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealsStatsDTO {
    private double kcal;
    private double proteins;
    private double fats;
    private double carbs;

    public MealsStatsDTO() {
        kcal = 0;
        proteins = 0;
        fats = 0;
        carbs = 0;
    }
}
