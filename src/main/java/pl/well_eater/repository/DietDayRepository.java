package pl.well_eater.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.well_eater.model.DietDayEntity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface DietDayRepository extends JpaRepository<DietDayEntity, Long> {
    Page<DietDayEntity> findAllByUsername(String username, Pageable pageable);
    Optional<DietDayEntity> findByDietDate(LocalDate dietDate);
    boolean existsByDietDateAndUsername(LocalDate dietDate, String username);

    @Query("SELECT d FROM DietDayEntity d WHERE d.dietDate BETWEEN :startDate AND :endDate AND d.username = :username")
    Set<DietDayEntity> findAllByDietDateBetweenAndUsername(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate,
                                                           @Param("username") String username);
}