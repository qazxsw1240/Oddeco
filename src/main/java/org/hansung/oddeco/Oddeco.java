package org.hansung.oddeco;

import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.Plugin;

@Plugin
public final class Oddeco extends JavaPlugin {
    @Override
    public void onDisable() {
        getLogger().info("Disabled Plugin [Oddeco]");
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabled Plugin [Oddeco]");
    }
}
