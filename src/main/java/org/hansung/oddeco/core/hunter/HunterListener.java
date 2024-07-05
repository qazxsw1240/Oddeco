package org.hansung.oddeco.core.hunter;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HunterListener implements Listener {
    JavaPlugin plugin;

    public HunterListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LivingEntity player = event.getPlayer();
        player.knockback(0.000000000000001, -0.1, -0.1);
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        event.setCancelled(true);
    }
}
