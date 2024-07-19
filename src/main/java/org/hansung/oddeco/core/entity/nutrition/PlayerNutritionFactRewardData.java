package org.hansung.oddeco.core.entity.nutrition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "player_nutrition_reward")
@ToString
public class PlayerNutritionFactRewardData {
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private String id;

    @Setter
    @Getter
    @Column(name = "nutrition_decrement", nullable = false)
    private int nutritionDecrement;

    @Setter
    @Getter
    @Column(name = "reward", nullable = false)
    private int reward;

    @Column(name = "delay_seconds", nullable = false)
    private int delaySeconds;

    public PlayerNutritionFactRewardData() {
    }

    public PlayerNutritionFactRewardData(
            String id,
            int nutritionDecrement,
            int reward,
            int delaySeconds) {
        this.id = id;
        this.nutritionDecrement = nutritionDecrement;
        this.reward = reward;
        this.delaySeconds = delaySeconds;
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.id);
    }

    public Duration getDelay() {
        return Duration.ofSeconds(this.delaySeconds);
    }

    public void setDelay(Duration delay) {
        this.delaySeconds = (int) delay.getSeconds();
    }
}
