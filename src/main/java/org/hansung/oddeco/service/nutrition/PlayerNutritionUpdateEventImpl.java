package org.hansung.oddeco.service.nutrition;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

class PlayerNutritionUpdateEventImpl implements PlayerNutritionUpdateEvent {
    private final Player player;
    private final NutritionFacts nutritionFacts;

    public PlayerNutritionUpdateEventImpl(Player player, NutritionFacts nutritionFacts) {
        this.player = player;
        this.nutritionFacts = nutritionFacts;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public NutritionFacts getUpdatedNutritionFactAmounts() {
        return this.nutritionFacts;
    }

    @Override
    public String toString() {
        return String.format("PlayerNutritionStateEvent(player=%s, nutritionFacts=%s)", this.player, this.nutritionFacts);
    }
}
