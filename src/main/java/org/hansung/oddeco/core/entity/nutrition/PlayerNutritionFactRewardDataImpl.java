package org.hansung.oddeco.core.entity.nutrition;

import java.util.UUID;

class PlayerNutritionFactRewardDataImpl implements PlayerNutritionFactRewardData {
    private final UUID uuid;
    private final int nutritionDecrement;
    private final int reward;

    public PlayerNutritionFactRewardDataImpl(
            UUID uuid,
            int nutritionDecrement,
            int reward) {
        this.uuid = uuid;
        this.nutritionDecrement = nutritionDecrement;
        this.reward = reward;
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
}
