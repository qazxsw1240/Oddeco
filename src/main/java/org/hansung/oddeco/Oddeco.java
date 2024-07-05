package org.hansung.oddeco;

import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.butcher.ButcherListener;
import org.hansung.oddeco.core.Plugin;

@Plugin
public final class Oddeco extends JavaPlugin {
    @Override
    public void onDisable() {
        this.getServer().getLogger().info("Disabled");
    }

    @Override
    public void onEnable() {
        this.getServer().getLogger().info("Enabled");
        this.getServer().getPluginManager().registerEvents(new ButcherListener(this), this);
    }
}
