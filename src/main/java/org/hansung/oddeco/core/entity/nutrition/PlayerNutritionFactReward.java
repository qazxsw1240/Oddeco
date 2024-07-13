package org.hansung.oddeco.core.entity.nutrition;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.player.PlayerNutritionState;

import java.time.LocalTime;

public interface PlayerNutritionFactReward {
    public static PlayerNutritionFactReward create(
            Player player,
            PlayerNutritionState nutritionState,
            LocalTime lastRewardTime,
            NutritionFacts nutritionFactDecrement) {
        return new PlayerNutritionFactRewardImpl(player, nutritionState, lastRewardTime, nutritionFactDecrement);
    }

    public abstract Player getPlayer();

    public abstract PlayerNutritionState getState();

    public abstract LocalTime getLastRewardTime();

    public abstract void setLastRewardTime(LocalTime lastRewardTime);

    public abstract NutritionFacts getNutritionFactDecrement();

    public abstract void setNutritionFactDecrement(NutritionFacts nutritionFactDecrement);
}
