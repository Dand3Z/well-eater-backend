package pl.well_eater.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.well_eater.dto.DietFoodDTO;
import pl.well_eater.dto.DietMacroDTO;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.model.MacroEntity;
import pl.well_eater.model.MealEntity;
import pl.well_eater.model.MealFoodEntity;
import pl.well_eater.repository.MealFoodRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealFoodService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final MealFoodRepository mealFoodRepository;
    private final FoodService foodService;

    DietMealDTO mapToDietMealDTO(MealEntity meal) {
        DietMealDTO mealDTO = new DietMealDTO();
        mealDTO.setMealId(meal.getId());
        mealDTO.setMealType(meal.getType());
        if(hasFoodsAttached(meal)) {
            meal.getMealFoods().forEach(mealFood -> {
                DietFoodDTO dietFood = mapToDietFoodDTO(mealFood.getFood(), mealFood.getId(), mealFood.getAmount());
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

    private DietFoodDTO mapToDietFoodDTO(FoodEntity food, long mealFoodId, double amount) {
        DietFoodDTO foodDto = new DietFoodDTO();
        foodDto.setFoodId(food.getId());
        foodDto.setName(food.getName());
        foodDto.setCategory(food.getCategory());
        foodDto.setFoodType(food.getType());
        foodDto.setUnit(food.getUnit());
        foodDto.setMealFoodId(mealFoodId);
        foodDto.setAmount(amount);
        foodDto.setMacros(mapToDietMacroDTO(food.getMacros()));
        return foodDto;
    }

    private DietMacroDTO mapToDietMacroDTO(MacroEntity macro) {
        DietMacroDTO macroDTO = new DietMacroDTO();
        macroDTO.setKcal(macro.getKcal());
        macroDTO.setProteins(macro.getProteins());
        macroDTO.setFats(macro.getFats());
        macroDTO.setCarbs(macro.getCarbs());
        return macroDTO;
    }

    private boolean hasFoodsAttached(MealEntity meal) {
        return !meal.getMealFoods().isEmpty();
    }

    @Transactional
    public DietMealDTO addFoodToMeal(MealEntity meal, Long foodId, double amount) {
        FoodEntity food = foodService.getFoodById(foodId);
        if (mealFoodRepository.existsByMealAndFood(meal, food)) {
            throw new EntityExistsException();
        }
        MealFoodEntity mealFood = new MealFoodEntity();
        mealFood.setMeal(meal);
        mealFood.setFood(food);
        mealFood.setAmount(amount);
        mealFoodRepository.save(mealFood);
        entityManager.flush();
        entityManager.refresh(meal);
        return mapToDietMealDTO(meal);
    }

    MealEntity findMealFor(long mealFoodId) {
        Optional<MealFoodEntity> mealFood = mealFoodRepository.findById(mealFoodId);
        if (mealFood.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return mealFood.get().getMeal();
    }

    public void removeFoodFromMeal(long mealFoodId) {
        mealFoodRepository.deleteById(mealFoodId);
    }

    @Transactional
    public DietMealDTO editFoodFromMeal(long mealFoodId, double newAmount){
        Optional<MealFoodEntity> mealFood = mealFoodRepository.findById(mealFoodId);
        if (mealFood.isEmpty()) {
            throw new EntityNotFoundException();
        }
        MealFoodEntity mealFoodEntity = mealFood.get();
        mealFoodEntity.setAmount(newAmount);
        mealFoodEntity = mealFoodRepository.save(mealFoodEntity);
        entityManager.flush();
        entityManager.refresh(mealFoodEntity);
        return mapToDietMealDTO(mealFoodEntity.getMeal());
    }

}
