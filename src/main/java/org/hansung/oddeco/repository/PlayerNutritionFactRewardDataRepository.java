package org.hansung.oddeco.repository;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardData;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardFactory;
import org.hansung.oddeco.core.sql.AbstractStatementExecutor;
import org.hansung.oddeco.core.sql.ResultMapper;

import java.sql.Connection;
import java.time.Duration;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayerNutritionFactRewardDataRepository
        extends AbstractStatementExecutor
        implements CrudRepository<PlayerNutritionFactRewardData, Player> {
    private final ConcurrentMap<UUID, PlayerNutritionFactRewardData> playerNutritionFactRewardData;

    public PlayerNutritionFactRewardDataRepository(Connection connection) {
        super(connection);
        this.playerNutritionFactRewardData = new ConcurrentSkipListMap<>();
    }

    private static String createNutritionStateRewardFindQuery(UUID uuid) {
        return String.format("SELECT * FROM player_nutrition_reward WHERE uuid = '%s'", uuid);
    }

    private static String createNutritionStateRewardCreateQuery(PlayerNutritionFactRewardData data) {
        UUID uuid = data.getPlayerUUID();
        return String.format(
                "INSERT INTO player_nutrition_reward (uuid, nutrition_decrement, reward, delay_seconds) VALUES ('%s', %d, %d, %d)",
                uuid,
                data.getNutritionDecrement(),
                data.getReward(),
                data.getDelay().toSeconds());
    }

    private static String createNutritionStateRewardUpdateQuery(PlayerNutritionFactRewardData data) {
        UUID uuid = data.getPlayerUUID();
        return String.format(
                "UPDATE player_nutrition_reward SET nutrition_decrement=%d, reward=%d, delay_seconds=%d where uuid = '%s'",
                data.getNutritionDecrement(),
                data.getReward(),
                data.getDelay().toSeconds(),
                uuid);
    }

    @Override
    public PlayerNutritionFactRewardData create(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerNutritionFactRewardData data = PlayerNutritionFactRewardFactory.createPlayerNutritionFactRewardData(uuid, 0, 0, Duration.ZERO);
        execute(createNutritionStateRewardCreateQuery(data));
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
        execute(createNutritionStateRewardUpdateQuery(element));
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
        return executeSingleQuery(createNutritionStateRewardFindQuery(uuid), createNutritionRewardData(uuid))
                .map(data -> {
                    this.playerNutritionFactRewardData.put(uuid, data);
                    return data;
                });
    }

    @Override
    public Collection<PlayerNutritionFactRewardData> getAll() {
        return this.playerNutritionFactRewardData.values();
    }

    private ResultMapper<PlayerNutritionFactRewardData> createNutritionRewardData(UUID uuid) {
        return resultSet -> {
            int decrement = resultSet.getInt("nutrition_decrement");
            int reward = resultSet.getInt("reward");
            int delaySeconds = resultSet.getInt("delay_seconds");
            Duration delay = Duration.ofSeconds(delaySeconds);
            return PlayerNutritionFactRewardFactory.createPlayerNutritionFactRewardData(uuid, decrement, reward, delay);
        };
    }
}
