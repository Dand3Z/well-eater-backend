package pl.well_eater.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.well_eater.model.DietDayEntity;
import pl.well_eater.model.MealEntity;
import pl.well_eater.model.MealType;
import pl.well_eater.repository.MealRepository;
import pl.well_eater.dto.DietMealDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealFoodService mealFoodService;

    public Set<DietMealDTO> initializeEmptyMealsForDietDay(DietDayEntity dietDay) {
        Set<MealEntity> meals = new HashSet<>();
        for(MealType mealType : MealType.values()) {
            MealEntity mealEntity = new MealEntity();
            mealEntity.setType(mealType);
            mealEntity.setDietDay(dietDay);
            meals.add(mealRepository.save(mealEntity));
        }
        return mealFoodService.mapToDietMealDTOs(meals);
    }

    DietMealDTO mapToDietMealDTO(MealEntity meal) {
        return mealFoodService.mapToDietMealDTO(meal);
    }

    Set<DietMealDTO> mapToDietMealDTOs(Collection<MealEntity> meals) {
        return mealFoodService.mapToDietMealDTOs(meals);
    }
}
