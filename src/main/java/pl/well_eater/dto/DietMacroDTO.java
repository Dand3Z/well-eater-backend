package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietMacroDTO {
    private double kcal;
    private double proteins;
    private double fats;
    private double carbs;
}
