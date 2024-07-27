package pl.well_eater.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.request.AddFoodToMealRequest;
import pl.well_eater.request.EditFoodInMealRequest;
import pl.well_eater.security.CurrentUser;
import pl.well_eater.service.MealService;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meal")
public class MealController {

    private final MealService mealService;

    @PostMapping("/add-food")
    ResponseEntity<?> addFoodToMeal(@RequestBody @NotNull final AddFoodToMealRequest request,
                                    @CurrentUser UserDetails principal) {
        try {
            DietMealDTO meal = mealService.addFoodToMeal(request.getMealId(), request.getFoodId(), request.getAmount(), principal);
            return ResponseEntity.created(new URI("/api/meal/get/" + meal.getMealId())).body(meal);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/edit-food/{mealFoodId}")
    ResponseEntity<?> editFoodInMeal(@Valid @PathVariable("mealFoodId") final Long mealFoodId,
                                     @RequestBody @NotNull final EditFoodInMealRequest request,
                                     @CurrentUser UserDetails principal) {
        try {
            DietMealDTO meal = mealService.editFoodFromMeal(mealFoodId, request.getAmount(), principal);
            return ResponseEntity.created(new URI("/api/meal/get/" + meal.getMealId())).body(meal);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete-food/{mealFoodId}")
    ResponseEntity<?> deleteFoodFromMeal(@Valid @PathVariable("mealFoodId") final Long mealFoodId,
                                         @CurrentUser UserDetails principal) {
        try {
            DietMealDTO mealDTO = mealService.removeFoodFromMeal(mealFoodId, principal);
            return ResponseEntity.ok(mealDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/get/{mealId}")
    ResponseEntity<?> getMealById(@PathVariable("mealId") final Long mealId,
                                  @CurrentUser UserDetails principal) {
        try {
            return ResponseEntity.ok(mealService.getMealById(mealId, principal));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }
}
