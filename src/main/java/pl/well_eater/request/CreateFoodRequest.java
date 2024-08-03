package pl.well_eater.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodType;
import pl.well_eater.model.UnitType;

@Getter
@Setter
@AllArgsConstructor
public class CreateFoodRequest {

    private String name;
    private FoodCategory category;
    private FoodType type;
    private UnitType unit;
    private double kcal;
    private double proteins;
    private double fats;
    private double carbs;
}
