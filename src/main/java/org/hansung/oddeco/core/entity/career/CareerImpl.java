package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

class CareerImpl implements Career {
    private final Player player;
    private final CareerCoins coins;

    public CareerImpl(Player player, CareerCoins coins) {
        this.player = player;
        this.coins = coins;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public CareerCoins getCoins() {
        return this.coins;
    }
}
