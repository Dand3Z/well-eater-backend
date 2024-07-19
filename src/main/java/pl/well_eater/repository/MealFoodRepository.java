package pl.well_eater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.well_eater.model.MealFoodEntity;

public interface MealFoodRepository extends JpaRepository<MealFoodEntity, Long> {
}
