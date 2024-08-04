package pl.well_eater.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.model.FoodType;
import pl.well_eater.model.MacroEntity;
import pl.well_eater.repository.FoodRepository;
import pl.well_eater.repository.MacroRepository;
import pl.well_eater.request.CreateFoodRequest;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.security.model.RoleEnum;

@Service
@RequiredArgsConstructor
public class FoodService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final FoodRepository foodRepository;
    private final MacroRepository macroRepository;

    public FoodEntity createFood(CreateFoodRequest request, UserDetails principal) {
        MacroEntity macro = new MacroEntity();
        macro = setMacroParams(macro, request);
        FoodEntity food = new FoodEntity();
        food.setToDelete(false);
        return setFoodParams(food, request, principal.getUsername(), macro);
    }

    public FoodEntity updateFoodById(CreateFoodRequest request, long foodId, UserDetails principal) {
        FoodEntity food = foodRepository.findById(foodId).orElseThrow(EntityNotFoundException::new);

        if(!isEditableByCurrentUser(food, principal)) {
            throw new UnauthorizedRequestException();
        }

        MacroEntity macro = food.getMacros();
        setMacroParams(macro, request);
        return setFoodParams(food, request);
    }

    private boolean isEditableByCurrentUser(FoodEntity food, UserDetails principal) {
        return isOwner(food, principal) || isAdmin(principal);
    }

    private boolean isAdmin(UserDetails principal) {
        return principal.getAuthorities().contains(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.toString()));
    }

    private boolean isOwner(FoodEntity food, UserDetails principal) {
        return principal.getUsername().equals(food.getAddedBy());
    }

    private MacroEntity setMacroParams(MacroEntity macro, CreateFoodRequest request) {
        macro.setKcal(request.getKcal());
        macro.setProteins(request.getProteins());
        macro.setFats(request.getFats());
        macro.setCarbs(request.getCarbs());
        return macroRepository.save(macro);
    }

    private FoodEntity setFoodParams(FoodEntity food, CreateFoodRequest request) {
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setType(request.getType());
        food.setUnit(request.getUnit());
        return foodRepository.save(food);
    }

    private FoodEntity setFoodParams(FoodEntity food, CreateFoodRequest request, String username, MacroEntity macro) {
        food.setAddedBy(username);
        food.setMacros(macro);
        return setFoodParams(food, request);
    }

    public FoodEntity getFoodById(Long foodId) {
        return foodRepository.findById(foodId).orElseThrow(EntityNotFoundException::new);
    }

    public Page<FoodEntity> getAllFoods(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }

    public void deleteFoodById(Long foodId, UserDetails principal) {
        if(!isAdmin(principal)) {
            throw new UnauthorizedRequestException();
        }
        foodRepository.deleteById(foodId);
    }

    public void markFoodToDeleteById(Long foodId, UserDetails principal) {
        FoodEntity food = getFoodById(foodId);

        if(!isEditableByCurrentUser(food, principal)) {
            throw new UnauthorizedRequestException();
        }

        food.setToDelete(true);
        foodRepository.save(food);
    }

    public void unmarkFoodToDeleteById(Long foodId, CreateFoodRequest request, UserDetails principal) {
        updateFoodById(request, foodId, principal);
        FoodEntity food = getFoodById(foodId);
        food.setToDelete(false);
        food.setAddedBy("system");
        foodRepository.save(food);
    }

    public Page<FoodEntity> searchFoodByType(FoodType type, Pageable pageable) {
        return foodRepository.findAllByType(type, pageable);
    }

    public Page<FoodEntity> searchFoodByCategory(FoodCategory category, Pageable pageable) {
        return foodRepository.findAllByCategory(category, pageable);
    }

    public Page<FoodEntity> searchFoodByCategoryAndType(FoodCategory category, FoodType type, Pageable pageable) {
        return foodRepository.findAllByCategoryAndType(category, type, pageable);
    }

    public Page<FoodEntity> searchFoodBySubstring(String substring, Pageable pageable) {
        return foodRepository.findAllByNameContainingIgnoreCase(substring, pageable);
    }

    public Page<FoodEntity> searchFoodAddedByCurrentUser(UserDetails principal, Pageable pageable) {
        return foodRepository.findAllByAddedByAndToDelete(principal.getUsername(), false, pageable);
    }

    public Page<FoodEntity> getAllMarkedToDelete(UserDetails principal, Pageable pageable) {
        if(!isAdmin(principal)) {
            throw new UnauthorizedRequestException();
        }
        return foodRepository.findAllByToDelete(true, pageable);
    }
}
