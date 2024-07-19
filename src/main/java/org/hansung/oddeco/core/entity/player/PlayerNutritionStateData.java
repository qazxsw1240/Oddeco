package org.hansung.oddeco.core.entity.player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "player_nutrition")
@ToString
public class PlayerNutritionStateData {
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private String id;

    @Getter
    @Setter
    @Column(nullable = false)
    private int carbohydrate;

    @Getter
    @Setter
    @Column(nullable = false)
    private int protein;

    @Getter
    @Setter
    @Column(nullable = false)
    private int fat;

    @Getter
    @Setter
    @Column(nullable = false)
    private int vitamin;

    public PlayerNutritionStateData() {
    }

    public PlayerNutritionStateData(
            String id,
            int carbohydrate,
            int protein,
            int fat,
            int vitamin) {
        this.id = id;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.vitamin = vitamin;
    }
}
