package pl.well_eater.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "foods")
public class FoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FoodCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FoodType type;

    @Column(name = "added_by")
    private String addedBy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "macronutrients_id", nullable = false, updatable = false)
    private MacroEntity macros;
}
