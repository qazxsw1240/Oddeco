package org.hansung.oddeco.core.entity.nutrition;

import java.time.Duration;
import java.util.UUID;

public interface PlayerNutritionFactRewardFactory {
    public static PlayerNutritionFactRewardData createPlayerNutritionFactRewardData(
            UUID uuid,
            int nutritionDecrement,
            int reward,
            Duration delay) {
        return new PlayerNutritionFactRewardData(uuid.toString(), nutritionDecrement, reward, (int) delay.toSeconds());
    }
}
