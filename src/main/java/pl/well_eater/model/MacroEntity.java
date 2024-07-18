package pl.well_eater.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "macronutrients")
public class MacroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "kcal", nullable = false)
    private double kcal;

    @Column(name = "proteins", nullable = false)
    private double proteins;

    @Column(name = "fats", nullable = false)
    private double fats;

    @Column(name = "carbs", nullable = false)
    private double carbs;
}
