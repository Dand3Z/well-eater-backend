package pl.well_eater.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.well_eater.model.FoodCategory;
import pl.well_eater.model.FoodEntity;
import pl.well_eater.model.FoodType;

import java.util.Set;

public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    Page<FoodEntity> findAllByCategory(FoodCategory category, Pageable pageable);
    Page<FoodEntity> findAllByType(FoodType type, Pageable pageable);
    Page<FoodEntity> findAllByCategoryAndType(FoodCategory category, FoodType type, Pageable pageable);
    Page<FoodEntity> findAllByNameContainingIgnoreCase(String partialName, Pageable pageable);
    Set<FoodEntity> findAllByAddedBy(String addedBy);
}
