package org.hansung.oddeco.core.entity.nutrition;

import java.util.UUID;

public interface PlayerNutritionFactRewardData {
    public abstract UUID getPlayerUUID();

    public abstract int getNutritionDecrement();

    public abstract int getReward();
}
