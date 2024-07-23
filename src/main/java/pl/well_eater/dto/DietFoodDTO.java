package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodType;
import pl.well_eater.model.UnitType;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DietFoodDTO {
    private long foodId;
    private String name;
    private FoodCategory category;
    private FoodType foodType;
    private UnitType unit;
    private double amount;
    private long mealFoodId;
    private DietMacroDTO macros;
}
