package pl.well_eater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.well_eater.model.MealFoodEntity;

public interface MealFoodRepository extends JpaRepository<MealFoodEntity, Long> {
    @Modifying
    @Query("DELETE FROM MealFoodEntity mf WHERE mf.id = :id")
    void deleteById(@Param("id") Long id);
}
