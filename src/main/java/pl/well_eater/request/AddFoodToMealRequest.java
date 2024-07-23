package pl.well_eater.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFoodToMealRequest {
    private long mealId;
    private long foodId;
    private double amount;
}
