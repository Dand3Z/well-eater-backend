package pl.well_eater.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.well_eater.dto.DietDayDTO;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.DietDayEntity;
import pl.well_eater.repository.DietDayRepository;
import pl.well_eater.request.CreateDietDayRequest;
import pl.well_eater.security.model.RoleEnum;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DietDayFacade {

    private final DietDayRepository dietDayRepository;
    private final MealService mealService;

    public DietDayDTO createDietDay(CreateDietDayRequest request, UserDetails principal) {
        DietDayEntity dietDayEntity = new DietDayEntity();
        dietDayEntity.setDietDate(request.getDietDate());
        dietDayEntity.setUsername(principal.getUsername());
        dietDayEntity = dietDayRepository.save(dietDayEntity);

        Set<DietMealDTO> meals = mealService.initializeEmptyMealsForDietDay(dietDayEntity);
        return mapToDietDayDTO(dietDayEntity, meals);
    }

    DietDayDTO mapToDietDayDTO(DietDayEntity dietDayEntity) {
        DietDayDTO dietDayDTO = new DietDayDTO();
        dietDayDTO.setDietDayId(dietDayEntity.getId());
        dietDayDTO.setDate(dietDayEntity.getDietDate());
        dietDayDTO.setUsername(dietDayEntity.getUsername());
        return dietDayDTO;
    }

    DietDayDTO mapToDietDayDTO(DietDayEntity dietDayEntity, Set<DietMealDTO> dietMealDtos) {
        DietDayDTO dietDayDTO = mapToDietDayDTO(dietDayEntity);
        dietDayDTO.setMeals(dietMealDtos);
        return dietDayDTO;
    }

    public void deleteDietDay(Long dietDayId, UserDetails principal) {
        Optional<DietDayEntity> dietDay = dietDayRepository.findById(dietDayId);
        if (dietDay.isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (!isEditableByCurrentUser(dietDay.get(), principal)) {
            throw new UnauthorizedRequestException();
        }
        dietDayRepository.deleteById(dietDayId);
    }

    private boolean isEditableByCurrentUser(DietDayEntity dietDay, UserDetails principal) {
        return principal.getUsername().equals(dietDay.getUsername()) || principal.getAuthorities().contains(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.toString()));
    }
}
