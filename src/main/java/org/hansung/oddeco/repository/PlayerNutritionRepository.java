package org.hansung.oddeco.repository;

import jakarta.persistence.EntityManager;
import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.nutrition.Nutrition;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;
import org.hansung.oddeco.core.entity.player.PlayerNutritionState;
import org.hansung.oddeco.core.entity.player.PlayerNutritionStateData;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayerNutritionRepository implements CrudRepository<PlayerNutritionState, Player> {
    private final ConcurrentMap<UUID, PlayerNutritionState> playerNutritionStates;
    private final EntityManager entityManager;

    public PlayerNutritionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.playerNutritionStates = new ConcurrentSkipListMap<>();
    }

    @Override
    public boolean contains(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerNutritionStateData data = this.entityManager.find(PlayerNutritionStateData.class, uuid.toString());
        return data != null;
    }

    @Override
    public Optional<PlayerNutritionState> get(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerNutritionStates.containsKey(uuid)) {
            return Optional.of(this.playerNutritionStates.get(uuid));
        }
        PlayerNutritionStateData data = this.entityManager.find(PlayerNutritionStateData.class, uuid.toString());
        return Optional.ofNullable(data)
                .map(stateData -> {
                    PlayerNutritionState wrapper = new ConnectionWrapper(this.entityManager, player, stateData);
                    this.playerNutritionStates.put(uuid, wrapper);
                    return wrapper;
                });
    }

    @Override
    public Collection<PlayerNutritionState> getAll() {
        return this.playerNutritionStates.values();
    }

    @Override
    public PlayerNutritionState create(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerNutritionStateData data = this.entityManager.find(PlayerNutritionStateData.class, uuid.toString());
        if (data == null) {
            data = new PlayerNutritionStateData(uuid.toString(), 0, 0, 0, 0);
            this.entityManager.persist(data);
        }
        ConnectionWrapper wrapper = new ConnectionWrapper(this.entityManager, player, data);
        this.playerNutritionStates.put(uuid, wrapper);
        return wrapper;
    }

    @Override
    public void insert(PlayerNutritionState element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(PlayerNutritionState element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Player player) {
        throw new UnsupportedOperationException();
    }

    private static class ConnectionWrapper
            implements PlayerNutritionState {
        private final EntityManager entityManager;
        private final Player player;
        private final PlayerNutritionStateData data;

        public ConnectionWrapper(EntityManager entityManager, Player player, PlayerNutritionStateData data) {
            this.entityManager = entityManager;
            this.player = player;
            this.data = data;
        }

        @Override
        public Player getPlayer() {
            return this.player;
        }

        @Override
        public int getAmount(Nutrition nutrition) {
            return switch (nutrition) {
                case CARBOHYDRATE -> this.data.getCarbohydrate();
                case PROTEIN -> this.data.getProtein();
                case FAT -> this.data.getFat();
                case VITAMIN -> this.data.getVitamin();
            };
        }

        @Override
        public void setAmount(Nutrition nutrition, int amount) {
            this.entityManager.getTransaction().begin();
            switch (nutrition) {
                case CARBOHYDRATE -> this.data.setCarbohydrate(amount);
                case PROTEIN -> this.data.setProtein(amount);
                case FAT -> this.data.setFat(amount);
                case VITAMIN -> this.data.setVitamin(amount);
            }
            this.entityManager.flush();
            this.entityManager.getTransaction().commit();
        }

        @Override
        public Map<Nutrition, Integer> getNutritionState() {
            Map<Nutrition, Integer> map = new EnumMap<>(Nutrition.class);
            map.put(Nutrition.CARBOHYDRATE, this.data.getCarbohydrate());
            map.put(Nutrition.PROTEIN, this.data.getProtein());
            map.put(Nutrition.FAT, this.data.getFat());
            map.put(Nutrition.VITAMIN, this.data.getVitamin());
            return map;
        }

        @Override
        public NutritionFacts asNutritionFacts() {
            return NutritionFacts.of(
                    this.data.getCarbohydrate(),
                    this.data.getProtein(),
                    this.data.getFat(),
                    this.data.getVitamin());
        }

        @Override
        public String toString() {
            return this.data.toString();
        }
    }
}
