package org.hansung.oddeco.core.entity.nutrition;

import java.time.Duration;
import java.util.UUID;

@Deprecated(forRemoval = true)
class PlayerNutritionFactRewardDataImpl {
    private final UUID uuid;
    private volatile int nutritionDecrement;
    private volatile int reward;
    private volatile Duration delay;

    public PlayerNutritionFactRewardDataImpl(
            UUID uuid,
            int nutritionDecrement,
            int reward,
            Duration delay) {
        this.uuid = uuid;
        this.nutritionDecrement = nutritionDecrement;
        this.reward = reward;
        this.delay = delay;
    }

    public UUID getPlayerUUID() {
        return this.uuid;
    }

    public int getNutritionDecrement() {
        return this.nutritionDecrement;
    }

    public void setNutritionDecrement(int nutritionDecrement) {
        this.nutritionDecrement = nutritionDecrement;
    }

    public int getReward() {
        return this.reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public Duration getDelay() {
        return this.delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return String.format(
                "PlayerNutritionFactRewardData(uuid=%s, nutritionDecrement=%s, reward=%s, delay=%s)",
                this.uuid,
                this.nutritionDecrement,
                this.reward,
                this.delay);
    }
}
