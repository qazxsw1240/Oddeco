package org.hansung.oddeco.service;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.hansung.oddeco.core.entity.career.CareerCoins;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.repository.AdvancementRewardRepository;
import org.hansung.oddeco.repository.CareerCoinsRepository;

public class PlayerCareerService implements Listener {
    private final Plugin plugin;
    private final CareerCoinsRepository careerCoinsRepository;
    private final AdvancementRewardRepository advancementRewardRepository;
    private final FormattedLogger logger;

    public PlayerCareerService(
            Plugin plugin,
            CareerCoinsRepository careerCoinsRepository,
            AdvancementRewardRepository advancementRewardRepository,
            FormattedLogger logger) {
        this.plugin = plugin;
        this.careerCoinsRepository = careerCoinsRepository;
        this.advancementRewardRepository = advancementRewardRepository;
        this.logger = logger;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMadeAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        CareerCoins careerCoins = this.careerCoinsRepository
                .get(player)
                .orElseThrow();
        this.advancementRewardRepository.get(advancement)
                .ifPresent(reward -> {
                    int coins = reward.getReward() + careerCoins.getCoins();
                    careerCoins.setCoins(coins);
                    this.logger.info("Successfully player gained %d of career coins", reward.getReward());
                    this.logger.info("Now player has %d of career coins", careerCoins.getCoins());
                });
    }
}
