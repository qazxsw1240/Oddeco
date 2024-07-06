package org.hansung.oddeco.service;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.hansung.oddeco.core.sql.AbstractStatementExecutor;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.repository.CareerCoinsRepository;

import java.sql.*;

public class PlayerCareerService extends AbstractStatementExecutor implements Listener {
    private final Plugin plugin;
    private final CareerCoinsRepository careerCoinsRepository;
    private final FormattedLogger logger;

    public PlayerCareerService(
            Plugin plugin,
            CareerCoinsRepository careerCoinsRepository,
            Connection connection,
            FormattedLogger logger) {
        super(connection);
        this.plugin = plugin;
        this.careerCoinsRepository = careerCoinsRepository;
        this.logger = logger;

        initializeSqlTable();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.careerCoinsRepository.contains(player)) {
            this.careerCoinsRepository.create(player);
        }
        this.careerCoinsRepository
                .get(player)
                .ifPresent(coins -> {
                    this.logger.info("Player %s successfully retrieved career coins info", player.getName());
                    this.logger.info("%s", coins);
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    private void initializeSqlTable() {
        try {
            DatabaseMetaData databaseMetaData = this.connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "player_career_coins", new String[]{"TABLE"});
            try (resultSet) {
                if (!resultSet.next()) {
                    try (Statement statement = this.connection.createStatement()) {
                        statement.execute("""
                                           create table player_career_coins
                                           (
                                               uuid   varchar not null constraint player_nutrition_pk primary key,
                                               amount integer not null
                                           )
                                          """);
                        statement.execute("""
                                          create unique index player_career_coins_uuid_uindex
                                              on player_career_coins (uuid)
                                          """);
                        this.logger.info("Successfully create table 'player_career_coins'");
                    }
                } else {
                    this.logger.info("found existing table 'player_career_coins'");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
