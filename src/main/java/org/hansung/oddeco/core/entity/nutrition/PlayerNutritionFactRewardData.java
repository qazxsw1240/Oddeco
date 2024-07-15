package org.hansung.oddeco.core.entity.nutrition;

import java.time.Duration;
import java.util.UUID;

public interface PlayerNutritionFactRewardData {
    public abstract UUID getPlayerUUID();

    public abstract int getNutritionDecrement();

    public abstract void setNutritionDecrement(int nutritionDecrement);

    public abstract int getReward();

    public abstract void setReward(int reward);

    public abstract Duration getDelay();

    public abstract void setDelay(Duration delay);
}
