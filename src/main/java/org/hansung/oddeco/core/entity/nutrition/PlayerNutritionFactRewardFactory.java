package org.hansung.oddeco.core.entity.nutrition;

import java.util.UUID;

public interface PlayerNutritionFactRewardFactory {
    public static PlayerNutritionFactRewardData createPlayerNutritionFactRewardData(
            UUID uuid,
            int nutritionDecrement,
            int reward) {
        return new PlayerNutritionFactRewardDataImpl(uuid, nutritionDecrement, reward);
    }
}
