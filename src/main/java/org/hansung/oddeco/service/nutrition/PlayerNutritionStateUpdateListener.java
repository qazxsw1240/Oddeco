package org.hansung.oddeco.service.nutrition;

public interface PlayerNutritionStateUpdateListener extends PlayerNutritionStateListener {
    public abstract void onPlayerNutritionStateUpdate(PlayerNutritionUpdateEvent event);
}
