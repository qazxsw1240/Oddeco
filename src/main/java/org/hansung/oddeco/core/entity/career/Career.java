package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

public interface Career {
    public abstract Player getPlayer();

    public abstract CareerCoins getCoins( );
}
