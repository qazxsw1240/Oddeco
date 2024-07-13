package org.hansung.oddeco.core.entity.nutrition;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.player.PlayerNutritionState;

import java.time.LocalTime;

class PlayerNutritionFactRewardImpl implements PlayerNutritionFactReward {
    private final Player player;
    private final PlayerNutritionState nutritionState;
    private volatile LocalTime lastRewardTime;
    private volatile NutritionFacts nutritionFactDecrement;

    public PlayerNutritionFactRewardImpl(
            Player player,
            PlayerNutritionState nutritionState,
            LocalTime lastRewardTime,
            NutritionFacts nutritionFactDecrement) {
        this.player = player;
        this.nutritionState = nutritionState;
        this.lastRewardTime = lastRewardTime;
        this.nutritionFactDecrement = nutritionFactDecrement;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public PlayerNutritionState getState() {
        return this.nutritionState;
    }

    @Override
    public LocalTime getLastRewardTime() {
        return this.lastRewardTime;
    }

    @Override
    public void setLastRewardTime(LocalTime lastRewardTime) {
        this.lastRewardTime = lastRewardTime;
    }

    @Override
    public NutritionFacts getNutritionFactDecrement() {
        return this.nutritionFactDecrement;
    }

    @Override
    public void setNutritionFactDecrement(NutritionFacts nutritionFactDecrement) {
        this.nutritionFactDecrement = nutritionFactDecrement;
    }
}
