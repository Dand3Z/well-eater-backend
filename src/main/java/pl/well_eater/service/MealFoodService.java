package pl.well_eater.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.well_eater.dto.DietFoodDTO;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.model.MealEntity;
import pl.well_eater.repository.MealFoodRepository;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealFoodService {

    private final MealFoodRepository mealFoodRepository;

    DietMealDTO mapToDietMealDTO(MealEntity meal) {
        DietMealDTO mealDTO = new DietMealDTO();
        mealDTO.setMealId(meal.getId());
        mealDTO.setMealType(meal.getType());
        if(hasFoodsAttached(meal)) {
            meal.getMealFoods().forEach(mealFood -> {
                DietFoodDTO dietFood = mapToDietFood(mealFood.getFood(), mealFood.getId(), mealFood.getAmount());
                mealDTO.getFoods().add(dietFood);
            });
        }
        return mealDTO;
    }

    Set<DietMealDTO> mapToDietMealDTOs(Collection<MealEntity> meals) {
        return meals.stream()
                .map(this::mapToDietMealDTO)
                .collect(Collectors.toSet());
    }

    private DietFoodDTO mapToDietFood(FoodEntity food, long mealFoodId, double amount) {
        DietFoodDTO foodDto = new DietFoodDTO();
        foodDto.setFoodId(food.getId());
        foodDto.setName(food.getName());
        foodDto.setCategory(food.getCategory());
        foodDto.setFoodType(food.getType());
        foodDto.setUnit(food.getUnit());
        foodDto.setMealFoodId(mealFoodId);
        foodDto.setAmount(amount);
        return foodDto;
    }

    private boolean hasFoodsAttached(MealEntity meal) {
        return !meal.getMealFoods().isEmpty();
    }
}
