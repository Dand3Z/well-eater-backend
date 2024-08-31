package pl.well_eater.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.well_eater.dto.MealsStatsDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.DietDayEntity;
import pl.well_eater.model.MacroEntity;
import pl.well_eater.model.MealEntity;
import pl.well_eater.model.MealType;
import pl.well_eater.repository.MealRepository;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.security.model.RoleEnum;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MealService {

    @PersistenceContext
    private final EntityManager entityManager;
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
        Set<DietMealDTO> mealsDtos = mealFoodService.mapToDietMealDTOs(meals);
        return mealsDtos;
    }

    public DietMealDTO addFoodToMeal(long mealId, long foodId, double amount, UserDetails principal) {
        MealEntity meal = mealRepository.findById(mealId).orElseThrow(EntityNotFoundException::new);
        if (!isEditableByCurrentUser(meal, principal)) {
            throw new UnauthorizedRequestException();
        }
        return mealFoodService.addFoodToMeal(meal, foodId, amount);
    }

    private boolean isEditableByCurrentUser(MealEntity meal, UserDetails principal) {
        return principal.getUsername().equals(meal.getDietDay().getUsername()) || principal.getAuthorities().contains(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.toString()));
    }

    @Transactional
    public DietMealDTO removeFoodFromMeal(long mealFoodId, UserDetails principal) {
        MealEntity meal = mealFoodService.findMealFor(mealFoodId);
        if (!isEditableByCurrentUser(meal, principal)) {
            throw new UnauthorizedRequestException();
        }
        mealFoodService.removeFoodFromMeal(mealFoodId);
        entityManager.clear();
        meal = mealRepository.findById(meal.getId()).orElseThrow(EntityNotFoundException::new);

        return mapToDietMealDTO(meal);
    }

    public DietMealDTO editFoodFromMeal(long mealFoodId, double newAmount, UserDetails principal) {
        MealEntity meal = mealFoodService.findMealFor(mealFoodId);
        if (!isEditableByCurrentUser(meal, principal)) {
            throw new UnauthorizedRequestException();
        }
        return mealFoodService.editFoodFromMeal(mealFoodId, newAmount);
    }

    public DietMealDTO getMealById(long mealId, UserDetails principal) {
        Optional<MealEntity> mealOptional = mealRepository.findById(mealId);
        if (mealOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (!isEditableByCurrentUser(mealOptional.get(), principal)) {
            throw new UnauthorizedRequestException();
        }
        return mealFoodService.mapToDietMealDTO(mealOptional.get());
    }
}
