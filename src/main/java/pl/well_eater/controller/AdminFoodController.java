package pl.well_eater.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.request.CreateFoodRequest;
import pl.well_eater.security.CurrentUser;
import pl.well_eater.service.FoodService;

import static pl.well_eater.controller.PageConfig.preparePageSortedByIdAsc;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/food")
public class AdminFoodController {

    private final FoodService foodService;

    @PatchMapping("/to-delete/unmark/{foodId}")
    public ResponseEntity<?> unmarkFoodToDelete(@Valid @PathVariable("foodId") final Long foodId,
                                                @RequestBody @NotNull final CreateFoodRequest request,
                                                @CurrentUser final UserDetails principal) {
        try {
            foodService.unmarkFoodToDeleteById(foodId, request, principal);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedRequestException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/get-all-to-delete")
    public ResponseEntity<?> getAllFoodToDelete(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @CurrentUser final UserDetails principal) {
        Page<FoodEntity> foodEntities = foodService.getAllMarkedToDelete(principal, preparePageSortedByIdAsc(page, size));
        return ResponseEntity.ok(foodEntities);
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
}
