package pl.well_eater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.well_eater.model.DietDayEntity;

public interface DietDayRepository extends JpaRepository<DietDayEntity, Long> {
}
