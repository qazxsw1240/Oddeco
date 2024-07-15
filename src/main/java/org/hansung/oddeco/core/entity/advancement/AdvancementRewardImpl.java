package org.hansung.oddeco.core.entity.advancement;

import org.bukkit.NamespacedKey;

class AdvancementRewardImpl implements AdvancementReward {
    private final NamespacedKey key;
    private final int reward;

    public AdvancementRewardImpl(NamespacedKey key, int reward) {
        this.key = key;
        this.reward = reward;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public int getReward() {
        return this.reward;
    }

    @Override
    public String toString() {
        return String.format("AdvancementReward(key=%s, reward=%d)", this.key, this.reward);
    }
}
