package org.hansung.oddeco.service.nutrition;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

public interface PlayerNutritionUpdateEvent {
    public static PlayerNutritionUpdateEvent of(Player player, NutritionFacts nutritionFacts) {
        return new PlayerNutritionUpdateEventImpl(player, nutritionFacts);
    }

    public abstract Player getPlayer();

    public abstract NutritionFacts getUpdatedNutritionFactAmounts();
}
