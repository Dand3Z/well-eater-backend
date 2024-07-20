package pl.well_eater.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.model.FoodType;
import pl.well_eater.request.CreateFoodRequest;
import pl.well_eater.security.CurrentUser;
import pl.well_eater.service.FoodService;

import java.net.URI;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    @PostMapping("/create")
    public ResponseEntity<?> createNewFood(@RequestBody @NotNull final CreateFoodRequest request,
                                          @CurrentUser final UserDetails principal) {
        try {
            FoodEntity food = foodService.createFood(request, principal);
            return ResponseEntity.created(new URI("/api/food/get/" + food.getId())).body(food);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get/{foodId}")
    public ResponseEntity<?> getFoodById(@Valid @PathVariable("foodId") final Long foodId) {
        try {
            FoodEntity food = foodService.getFoodById(foodId);
            return ResponseEntity.ok(food);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{foodId}")
    public ResponseEntity<?> updateFoodById(@RequestBody @NotNull final CreateFoodRequest request,
                                            @Valid @PathVariable("foodId") final Long foodId,
                                            @CurrentUser final UserDetails principal
                                            ) {
        try {
            FoodEntity food = foodService.updateFoodById(request, foodId, principal);
            return ResponseEntity.ok(food);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @DeleteMapping("/delete/{foodId}")
    public ResponseEntity<?> deleteFoodById(@Valid @PathVariable("foodId") final Long foodId,
                                            @CurrentUser final UserDetails principal) {
        try {
            foodService.deleteFoodById(foodId, principal);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/search/by-category")
    public ResponseEntity<?> searchFoodByCategory(@RequestParam FoodCategory category,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<FoodEntity> foodEntities = foodService.searchFoodByCategory(category, preparePage(page, size));
        return ResponseEntity.ok(foodEntities);
    }

    private Pageable preparePage(int page, int size) {
        Sort sortOrder = Sort.by(Sort.Direction.ASC, "name");
        return PageRequest.of(page, size, sortOrder);
    }

    @GetMapping("/search/by-type")
    public ResponseEntity<?> searchFoodByType(@RequestParam FoodType type,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Page<FoodEntity> foodEntities = foodService.searchFoodByType(type, preparePage(page, size));
        return ResponseEntity.ok(foodEntities);
    }

    @GetMapping("/search/by-category-and-type")
    public ResponseEntity<?> searchFoodByCategoryAndType(
                                                @RequestParam FoodCategory category,
                                                @RequestParam FoodType type,
                                                @RequestParam(defaultValue = "0") int page,
                                     @          RequestParam(defaultValue = "10") int size) {
        Page<FoodEntity> foodEntities = foodService.searchFoodByCategoryAndType(category, type, preparePage(page, size));
        return ResponseEntity.ok(foodEntities);
    }

    @GetMapping("/search/by-text")
    public ResponseEntity<?> searchFoodBySubstring(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<FoodEntity> foodEntities = foodService.searchFoodBySubstring(text, preparePage(page, size));
        return ResponseEntity.ok(foodEntities);
    }

    @GetMapping("/search/created-by-me")
    public ResponseEntity<?> searchFoodAddedByCurrentUser(@CurrentUser final UserDetails principal) {
        Set<FoodEntity> foodEntities = foodService.searchFoodAddedByCurrentUser(principal);
        return ResponseEntity.ok(foodEntities);
    }
}
