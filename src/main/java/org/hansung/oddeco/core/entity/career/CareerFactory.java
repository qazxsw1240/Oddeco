package org.hansung.oddeco.core.entity.career;

import org.bukkit.entity.Player;

import java.util.Set;

public interface CareerFactory {
    public static CareerFactory create() {
        return new CareerFactoryImpl();
    }

    public abstract Career createCareer(Player player, CareerCoins coins, Set<? extends CareerPermission> permissions);
}
