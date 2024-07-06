package org.hansung.oddeco.repository;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.career.CareerCoins;
import org.hansung.oddeco.core.sql.AbstractStatementExecutor;
import org.hansung.oddeco.core.sql.ResultMapper;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class CareerCoinsRepository
        extends AbstractStatementExecutor
        implements CrudRepository<CareerCoins, Player> {
    private final ConcurrentMap<UUID, CareerCoins> coins;

    public CareerCoinsRepository(Connection connection) {
        super(connection);
        this.coins = new ConcurrentSkipListMap<>();
    }

    private static String createCareerCoinsFindQuery(UUID uuid) {
        return String.format("SELECT * FROM player_career_coins WHERE uuid = '%s'", uuid);
    }

    private static String createCareerCoinsCreateQuery(Player player, CareerCoins coins) {
        UUID uuid = player.getUniqueId();
        return String.format(
                "INSERT INTO player_career_coins (uuid, amount) VALUES ('%s', %d)",
                uuid,
                coins.getCoins());
    }

    @Override
    public boolean contains(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.coins.containsKey(uuid)) {
            return true;
        }
        return executeSingleQuery(createCareerCoinsFindQuery(uuid), set -> set.getString(1))
                .filter(value -> value.equals(player.getUniqueId().toString()))
                .isPresent();
    }

    @Override
    public Optional<CareerCoins> get(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.coins.containsKey(uuid)) {
            return Optional.of(this.coins.get(uuid));
        }
        return executeSingleQuery(createCareerCoinsFindQuery(uuid), createCareerCoins())
                .map(coins -> {
                    CareerCoins wrapper = new ConnectionWrapper(this.connection, player, coins);
                    this.coins.put(uuid, wrapper);
                    return coins;
                });
    }

    @Override
    public Collection<CareerCoins> getAll() {
        return this.coins.values();
    }

    @Override
    public CareerCoins create(Player player) {
        UUID uuid = player.getUniqueId();
        CareerCoins coins = CareerCoins.of();
        execute(createCareerCoinsCreateQuery(player, coins));
        CareerCoins wrapper = new ConnectionWrapper(this.connection, player, coins);
        this.coins.put(uuid, wrapper);
        return wrapper;
    }

    @Override
    public void insert(CareerCoins element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(CareerCoins element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Player key) {
        throw new UnsupportedOperationException();
    }

    private ResultMapper<CareerCoins> createCareerCoins() {
        return resultSet -> {
            int coins = resultSet.getInt(2);
            return CareerCoins.of(coins);
        };
    }

    private static class ConnectionWrapper
            extends AbstractStatementExecutor
            implements CareerCoins {
        private final Player player;
        private final CareerCoins careerCoins;

        public ConnectionWrapper(
                Connection connection,
                Player player,
                CareerCoins careerCoins) {
            super(connection);
            this.player = player;
            this.careerCoins = careerCoins;
        }

        @Override
        public int getCoins() {
            return this.careerCoins.getCoins();
        }

        @Override
        public void setCoins(int coins) {
            this.careerCoins.setCoins(coins);
            UUID uuid = this.player.getUniqueId();
            String sql = String.format("UPDATE player_career_coins SET amount=%d WHERE uuid='%s'", coins, uuid);
            execute(sql);
        }

        @Override
        public String toString() {
            return this.careerCoins.toString();
        }
    }
}
