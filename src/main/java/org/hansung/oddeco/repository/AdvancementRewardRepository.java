package org.hansung.oddeco.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.hansung.oddeco.core.entity.advancement.AdvancementReward;

import java.util.*;

public class AdvancementRewardRepository implements ReadonlyRepository<AdvancementReward, Advancement> {
    private final Map<NamespacedKey, AdvancementReward> rewards;

    public AdvancementRewardRepository(JsonArray jsonArray) {
        this.rewards = new TreeMap<>();
        jsonArray.forEach(element -> {
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Illegal json format to parse advancement reward: " + element);
            }
            JsonObject jsonObject = element.getAsJsonObject();
            NamespacedKey key = NamespacedKey.fromString(jsonObject
                    .get("key")
                    .getAsString());
            int reward = jsonObject.get("reward").getAsInt();
            AdvancementReward advancementReward = AdvancementReward.of(key, reward);
            this.rewards.put(key, advancementReward);
        });
    }

    @Override
    public boolean contains(Advancement advancement) {
        return get(advancement).isPresent();
    }

    @Override
    public Optional<AdvancementReward> get(Advancement advancement) {
        return Optional.ofNullable(this.rewards.get(advancement.getKey()));
    }

    @Override
    public Collection<AdvancementReward> getAll() {
        return Collections.unmodifiableCollection(this.rewards.values());
    }
}
