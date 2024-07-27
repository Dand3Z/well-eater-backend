package pl.well_eater.dto;

import lombok.Getter;
import lombok.Setter;
import pl.well_eater.model.MealType;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DietMealDTO {
    private Long mealId;
    private MealType mealType;
    private Set<DietFoodDTO> foods = new HashSet<>();
    private MealsStatsDTO stats;
}
