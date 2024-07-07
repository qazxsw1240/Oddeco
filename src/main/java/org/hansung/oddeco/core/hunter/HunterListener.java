package org.hansung.oddeco.core.hunter;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterListener implements Listener {
    private static final Logger log = LoggerFactory.getLogger(HunterListener.class);
    private final JavaPlugin plugin;
    private final FormattedLogger logger;
    private final ConcurrentMap<LivingEntity, Hunter> hunters;


    public HunterListener(JavaPlugin plugin, FormattedLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.hunters = new ConcurrentHashMap<>();
        logger.info("HunterListener Registed.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        hunters.put(event.getPlayer(), new Hunter(1));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (hunters.containsKey(player)) {
            player.heal(1);
        }
    }
}
