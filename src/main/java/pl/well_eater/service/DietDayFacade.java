package pl.well_eater.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.well_eater.dto.DayStatsDTO;
import pl.well_eater.dto.DaysStatsDTO;
import pl.well_eater.dto.DietDayDTO;
import pl.well_eater.dto.DietMealDTO;
import pl.well_eater.dto.MealsStatsDTO;
import pl.well_eater.exception.EntityNotFoundException;
import pl.well_eater.exception.UnauthorizedRequestException;
import pl.well_eater.model.DietDayEntity;
import pl.well_eater.repository.DietDayRepository;
import pl.well_eater.request.CreateDietDayRequest;
import pl.well_eater.security.model.RoleEnum;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietDayFacade {

    private final DietDayRepository dietDayRepository;
    private final MealService mealService;
    private final MealFoodService mealFoodService;

    public DietDayDTO createDietDay(CreateDietDayRequest request, UserDetails principal) {
        if (dietDayRepository.existsByDietDateAndUsername(request.getDietDate(), principal.getUsername())) {
            throw new EntityExistsException();
        }
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

    Set<DietDayDTO> mapToDietDayDtos(Collection<DietDayEntity> dietDays) {
        return dietDays.stream()
                .map(dietDay -> mapToDietDayDTO(dietDay, mealFoodService.mapToDietMealDTOs(dietDay.getMeals())))
                .collect(Collectors.toSet());
    }

    public DietDayDTO getDietDay(Long dietDayId, UserDetails principal) {
        Optional<DietDayEntity> optionalDay = dietDayRepository.findById(dietDayId);
        if (optionalDay.isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (!isEditableByCurrentUser(optionalDay.get(), principal)) {
            throw new UnauthorizedRequestException();
        }
        DietDayEntity dietDayEntity = optionalDay.get();
        DietDayDTO dietDayDTO = mapToDietDayDTO(dietDayEntity, mealService.mapToDietMealDTOs(dietDayEntity.getMeals()));
        dietDayDTO.setStats(calculateDayStats(dietDayEntity));
        return dietDayDTO;
    }

    public Set<DietDayDTO> getDietDaysBetween(LocalDate startDate, LocalDate endDate, UserDetails principal) {
        Set<DietDayEntity> dietDays = dietDayRepository.findAllByDietDateBetweenAndUsername(startDate, endDate, principal.getUsername());
        Set<DietDayDTO> dietDayDTOs = mapToDietDayDtos(dietDays);
        dietDayDTOs.forEach(dietDayDto ->
                dietDayDto.setStats(calculateDayStats(dietDays.stream()
                        .filter(dietDay -> Objects.equals(dietDayDto.getDietDayId(), dietDay.getId()))
                        .findFirst()
                        .orElseThrow())));
        return dietDayDTOs;
    }


    public void deleteDietDay(Long dietDayId, UserDetails principal) {
        Optional<DietDayEntity> optionalDay = dietDayRepository.findById(dietDayId);
        if (optionalDay.isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (!isEditableByCurrentUser(optionalDay.get(), principal)) {
            throw new UnauthorizedRequestException();
        }
        dietDayRepository.deleteById(dietDayId);
    }

    private boolean isEditableByCurrentUser(DietDayEntity dietDay, UserDetails principal) {
        return principal.getUsername().equals(dietDay.getUsername()) || principal.getAuthorities().contains(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.toString()));
    }

    public DayStatsDTO calculateDayStats(Long dietDayId, UserDetails principal) {
        Optional<DietDayEntity> optionalDay = dietDayRepository.findById(dietDayId);
        if (optionalDay.isEmpty()) {
            throw new EntityNotFoundException();
        }
        DietDayEntity dietDay = optionalDay.get();
        if (!isEditableByCurrentUser(dietDay, principal)) {
            throw new UnauthorizedRequestException();
        }
        return calculateDayStats(dietDay);
    }

    private DayStatsDTO calculateDayStats(DietDayEntity dietDay) {
        DayStatsDTO statsDTO = new DayStatsDTO();
        statsDTO.setDate(dietDay.getDietDate());
        statsDTO.setStats(mealFoodService.calculateMealsStats(dietDay.getMeals()));
        return statsDTO;
    }

    public DaysStatsDTO calculateDaysStats(LocalDate dateFrom, LocalDate dateTo, UserDetails principal) {
        Set<DietDayEntity> dietDays = dietDayRepository.findAllByDietDateBetweenAndUsername(dateFrom, dateTo, principal.getUsername());
        DaysStatsDTO daysStatsDTO = new DaysStatsDTO(dateFrom, dateTo, new MealsStatsDTO());
        MealsStatsDTO totalStats = daysStatsDTO.getStats();
        dietDays.stream()
                .map(this::calculateDayStats)
                .forEach(dayStats -> {
                    MealsStatsDTO currentStats = dayStats.getStats();
                    totalStats.setKcal(totalStats.getKcal() + currentStats.getKcal());
                    totalStats.setProteins(totalStats.getProteins() + currentStats.getProteins());
                    totalStats.setFats(totalStats.getFats() + currentStats.getFats());
                    totalStats.setCarbs(totalStats.getCarbs() + currentStats.getCarbs());
                });
        totalStats.setKcal(setDoubleDecimalPlaces(totalStats.getKcal() / dietDays.size()));
        totalStats.setProteins(setDoubleDecimalPlaces(totalStats.getProteins() / dietDays.size()));
        totalStats.setFats(setDoubleDecimalPlaces(totalStats.getFats() / dietDays.size()));
        totalStats.setCarbs(setDoubleDecimalPlaces(totalStats.getCarbs() / dietDays.size()));
        return daysStatsDTO;
    }

    private double setDoubleDecimalPlaces(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
