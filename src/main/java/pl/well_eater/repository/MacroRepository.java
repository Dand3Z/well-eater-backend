package pl.well_eater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.well_eater.model.MacroEntity;

public interface MacroRepository extends JpaRepository<MacroEntity, Long> {

}
