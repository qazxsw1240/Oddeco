package org.hansung.oddeco.repository;

import jakarta.persistence.EntityManager;
import org.bukkit.entity.Player;
import org.hansung.oddeco.core.CrudRepository;
import org.hansung.oddeco.core.entity.career.CareerCoinsData;
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
    private final EntityManager entityManager;

    public CareerCoinsRepository(Connection connection, EntityManager entityManager) {
        super(connection);
        this.entityManager = entityManager;
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
        return get(player).isPresent();
    }

    @Override
    public Optional<CareerCoins> get(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.coins.containsKey(uuid)) {
            return Optional.of(this.coins.get(uuid));
        }
        return Optional
                .ofNullable(this.entityManager.find(CareerCoinsData.class, uuid.toString()))
                .map(coins -> {
                    CareerCoins wrapper = new ConnectionWrapper(this.entityManager, coins);
                    this.coins.put(uuid, wrapper);
                    return wrapper;
                });
    }

    @Override
    public Collection<CareerCoins> getAll() {
        return this.coins.values();
    }

    @Override
    public CareerCoins create(Player player) {
        UUID uuid = player.getUniqueId();
        CareerCoinsData data = new CareerCoinsData(uuid.toString(), 0);
        this.entityManager.persist(data);
        CareerCoins wrapper = new ConnectionWrapper(this.entityManager, data);
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

    private static class ConnectionWrapper implements CareerCoins {
        private final EntityManager entityManager;
        private final CareerCoinsData data;

        public ConnectionWrapper(EntityManager entityManager, CareerCoinsData data) {
            this.entityManager = entityManager;
            this.data = data;
        }

        @Override
        public int getCoins() {
            return this.data.getCoins();
        }

        @Override
        public void setCoins(int coins) {
            this.entityManager.getTransaction().begin();
            this.data.setCoins(coins);
            this.entityManager.merge(this.data);
            this.entityManager.getTransaction().commit();
        }

        @Override
        public String toString() {
            return this.data.toString();
        }
    }
}
