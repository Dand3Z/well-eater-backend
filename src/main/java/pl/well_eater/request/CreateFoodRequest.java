package pl.well_eater.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodType;

@Getter
@Setter
@AllArgsConstructor
public class CreateFoodRequest {

    private String name;
    private FoodCategory category;
    private FoodType type;
    private double kcal;
    private double proteins;
    private double fats;
    private double carbs;
}
