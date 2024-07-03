package org.hansung.oddeco.core.util.logging;

import org.bukkit.plugin.Plugin;

public interface PluginLoggerFactory {
    public static FormattedLogger getLogger(Plugin plugin) {
        return new FormattedLoggerImpl(plugin.getLogger());
    }
}
