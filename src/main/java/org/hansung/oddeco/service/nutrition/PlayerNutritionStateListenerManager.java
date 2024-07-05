package org.hansung.oddeco.service.nutrition;

public interface PlayerNutritionStateListenerManager {
    public abstract void addListener(PlayerNutritionStateListener listener);

    public abstract void removeListener(PlayerNutritionStateListener listener);

    public abstract void removeAllListeners();
}
