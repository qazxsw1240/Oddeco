package org.hansung.oddeco.core.entity.nutrition;

import java.time.Duration;
import java.util.UUID;

class PlayerNutritionFactRewardDataImpl implements PlayerNutritionFactRewardData {
    private final UUID uuid;
    private final int nutritionDecrement;
    private final int reward;
    private final Duration delay;

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

    @Override
    public UUID getPlayerUUID() {
        return this.uuid;
    }

    @Override
    public int getNutritionDecrement() {
        return this.nutritionDecrement;
    }

    @Override
    public int getReward() {
        return this.reward;
    }

    @Override
    public Duration getDelay() {
        return this.delay;
    }
}
