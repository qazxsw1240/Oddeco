package org.hansung.oddeco.core.entity.advancement;

import org.bukkit.NamespacedKey;

public interface AdvancementReward {
    public static AdvancementReward of(NamespacedKey key, int reward) {
        return new AdvancementRewardImpl(key, reward);
    }

    public abstract NamespacedKey getKey();

    public abstract int getReward();
}
