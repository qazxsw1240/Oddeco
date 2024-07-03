package org.hansung.oddeco.core.entity.player;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.nutrition.Nutrition;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

import java.util.Map;

public interface PlayerNutritionState {
    public static PlayerNutritionState of(Player player) {
        return new PlayerNutritionStateImpl(player);
    }

    public abstract Player getPlayer();

    public abstract int getAmount(Nutrition nutrition);

    public abstract void setAmount(Nutrition nutrition, int amount);

    public abstract Map<Nutrition, Integer> getNutritionState();

    public abstract NutritionFacts asNutritionFacts();
}
