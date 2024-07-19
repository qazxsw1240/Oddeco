package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

import java.util.Set;

class CareerFactoryImpl implements CareerFactory {
    @Override
    public Career createCareer(Player player, CareerCoins coins, Set<? extends CareerPermission> permissions) {
        return new CareerImpl(player, coins, permissions);
    }
}
