package org.hansung.oddeco.service;

import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.hansung.oddeco.core.entity.advancement.AdvancementReward;
import org.hansung.oddeco.core.util.logging.FormattedLogger;

public class PlayerAdvancementRewardService implements Listener {
    private final ReadonlyRepository<AdvancementReward, Advancement> advancementRewardRepository;
    private final FormattedLogger logger;

    public PlayerAdvancementRewardService(
            ReadonlyRepository<AdvancementReward, Advancement> advancementRewardRepository,
            FormattedLogger logger) {
        this.advancementRewardRepository = advancementRewardRepository;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMadeAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        this.advancementRewardRepository.get(advancement)
                .ifPresent(reward -> this.logger.info("advancement: %s", reward));
    }
}
