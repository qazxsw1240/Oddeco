package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

class CareerImpl implements Career {
    private final Player player;
    private final CareerCoins coins;
    private volatile Set<? extends CareerPermission> permissions;

    public CareerImpl(Player player, CareerCoins coins) {
        this(player, coins, Collections.emptySet());
    }

    public CareerImpl(Player player, CareerCoins coins, Set<? extends CareerPermission> permissions) {
        this.player = player;
        this.coins = coins;
        this.permissions = permissions;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public CareerCoins getCoins() {
        return this.coins;
    }

    @Override
    public Set<? extends CareerPermission> getPermissions() {
        return this.permissions;
    }

    @Override
    public void setPermissions(Set<? extends CareerPermission> permissions) {
        this.permissions = permissions;
    }
}
