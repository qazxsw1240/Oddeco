package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

import java.util.Set;

public interface Career {
    public abstract Player getPlayer();

    public abstract CareerCoins getCoins();

    public abstract Set<? extends CareerPermission> getPermissions();

    public abstract void setPermissions(Set<? extends CareerPermission> permissions);
}
