package org.hansung.oddeco.repository;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.nutrition.Nutrition;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;
import org.hansung.oddeco.core.entity.player.PlayerNutritionState;
import org.hansung.oddeco.core.sql.AbstractStatementExecutor;
import org.hansung.oddeco.core.sql.ResultMapper;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayerNutritionRepository
        extends AbstractStatementExecutor
        implements CrudRepository<PlayerNutritionState, Player> {
    private final ConcurrentMap<UUID, PlayerNutritionState> playerNutritionStates;

    public PlayerNutritionRepository(Connection connection) {
        super(connection);
        this.playerNutritionStates = new ConcurrentSkipListMap<>();
    }

    private static String createNutritionStateFindQuery(UUID uuid) {
        return String.format("SELECT * FROM player_nutrition WHERE uuid = '%s'", uuid);
    }

    private static String createNutritionStateCreateQuery(PlayerNutritionState state) {
        UUID uuid = state.getPlayer().getUniqueId();
        return String.format(
                "INSERT INTO player_nutrition (uuid, carbohydrate, protein, fat, vitamin) VALUES ('%s', %d, %d, %d, %d)",
                uuid,
                state.getAmount(Nutrition.CARBOHYDRATE),
                state.getAmount(Nutrition.PROTEIN),
                state.getAmount(Nutrition.FAT),
                state.getAmount(Nutrition.VITAMIN));
    }

    @Override
    public boolean contains(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerNutritionStates.containsKey(uuid)) {
            return true;
        }
        return executeSingleQuery(createNutritionStateFindQuery(uuid), set -> set.getString(1))
                .filter(value -> value.equals(player.getUniqueId().toString()))
                .isPresent();
    }

    @Override
    public Optional<PlayerNutritionState> get(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerNutritionStates.containsKey(uuid)) {
            return Optional.of(this.playerNutritionStates.get(uuid));
        }
        return executeSingleQuery(createNutritionStateFindQuery(uuid), createNutritionState(player))
                .map(state -> {
                    PlayerNutritionState wrapper = new ConnectionWrapper(this.connection, state);
                    this.playerNutritionStates.put(uuid, wrapper);
                    return state;
                });
    }

    @Override
    public Collection<PlayerNutritionState> getAll() {
        return this.playerNutritionStates.values();
    }

    @Override
    public PlayerNutritionState create(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerNutritionState state = PlayerNutritionState.of(player);
        execute(createNutritionStateCreateQuery(state));
        ConnectionWrapper wrapper = new ConnectionWrapper(this.connection, state);
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

    private ResultMapper<PlayerNutritionState> createNutritionState(Player player) {
        return resultSet -> {
            PlayerNutritionState state = PlayerNutritionState.of(player);
            state.setAmount(Nutrition.CARBOHYDRATE, resultSet.getInt(2));
            state.setAmount(Nutrition.PROTEIN, resultSet.getInt(3));
            state.setAmount(Nutrition.FAT, resultSet.getInt(3));
            state.setAmount(Nutrition.VITAMIN, resultSet.getInt(4));
            return state;
        };
    }

    private static class ConnectionWrapper
            extends AbstractStatementExecutor
            implements PlayerNutritionState {
        private final PlayerNutritionState nutritionState;

        public ConnectionWrapper(Connection connection, PlayerNutritionState nutritionState) {
            super(connection);
            this.nutritionState = nutritionState;
        }

        @Override
        public Player getPlayer() {
            return this.nutritionState.getPlayer();
        }

        @Override
        public int getAmount(Nutrition nutrition) {
            return this.nutritionState.getAmount(nutrition);
        }

        @Override
        public void setAmount(Nutrition nutrition, int amount) {
            this.nutritionState.setAmount(nutrition, amount);
            UUID uuid = this.nutritionState.getPlayer().getUniqueId();
            String sql = String.format("UPDATE player_nutrition SET %s=%d WHERE uuid='%s'", nutrition
                    .name()
                    .toLowerCase(), amount, uuid);
            System.out.println("executed sql: " + sql);
            execute(sql);
        }

        @Override
        public Map<Nutrition, Integer> getNutritionState() {
            return this.nutritionState.getNutritionState();
        }

        @Override
        public NutritionFacts asNutritionFacts() {
            return this.nutritionState.asNutritionFacts();
        }

        @Override
        public String toString() {
            return this.nutritionState.toString();
        }
    }
}
