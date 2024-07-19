package org.hansung.oddeco.repository;

import jakarta.persistence.EntityManager;
import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardData;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayerNutritionFactRewardDataRepository implements CrudRepository<PlayerNutritionFactRewardData, Player> {
    private final ConcurrentMap<UUID, PlayerNutritionFactRewardData> playerNutritionFactRewardData;
    private final EntityManager entityManager;

    public PlayerNutritionFactRewardDataRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.playerNutritionFactRewardData = new ConcurrentSkipListMap<>();
    }

    @Override
    public PlayerNutritionFactRewardData create(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerNutritionFactRewardData data = PlayerNutritionFactRewardFactory.createPlayerNutritionFactRewardData(uuid, 0, 0, Duration.ZERO);
        this.entityManager.persist(data);
        this.playerNutritionFactRewardData.put(uuid, data);
        return data;
    }

    @Override
    public void insert(PlayerNutritionFactRewardData element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(PlayerNutritionFactRewardData element) {
        UUID uuid = element.getPlayerUUID();
        if (!this.playerNutritionFactRewardData.containsKey(uuid)) {
            throw new NoSuchElementException("Cannot update nutrition fact reward data for unknown player");
        }
        this.entityManager.merge(element);
        this.playerNutritionFactRewardData.put(uuid, element);
    }

    @Override
    public void remove(Player player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Player player) {
        return get(player).isPresent();
    }

    @Override
    public Optional<PlayerNutritionFactRewardData> get(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerNutritionFactRewardData.containsKey(uuid)) {
            return Optional.of(this.playerNutritionFactRewardData.get(uuid));
        }
        return Optional
                .ofNullable(this.entityManager.find(PlayerNutritionFactRewardData.class, uuid.toString()))
                .map(data -> {
                    this.playerNutritionFactRewardData.put(uuid, data);
                    return data;
                });
    }

    @Override
    public Collection<PlayerNutritionFactRewardData> getAll() {
        return this.playerNutritionFactRewardData.values();
    }
}
